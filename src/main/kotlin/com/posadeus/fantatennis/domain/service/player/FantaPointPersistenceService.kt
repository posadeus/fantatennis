package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.controller.model.ranking.RankedPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceSucceedWithErrors
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceSuccess
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceFailure
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceSuccess
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import org.slf4j.LoggerFactory

class FantaPointPersistenceService(private val persistPlayersPointsRepository: PersistPlayersPointsRepository,
                                   private val retrievePlayerService: RetrievePlayerService,
                                   private val rankingService: RankingService,
                                   private val persistPlayerService: PersistPlayerService) {

  fun persistScores(players: Set<AtpPlayer>) {

    if (players.isNotEmpty())
      persistPlayersPointsRepository.persistAll(players)
  }

  fun persist(players: Set<AtpPlayer>): FantaPointPersistence {

    val allPlayersByAtpId = retrievePlayerService.allPlayers()
        .groupBy { it.atpId }

    val notRegisteredPlayersIds = players
        .filterNot { it.id in allPlayersByAtpId.keys }
        .map { it.id }
        .toSet()

    return if (notRegisteredPlayersIds.isEmpty())
      persistPlayersPoints(players)
    else {

      LOGGER.warn("Missing players: $notRegisteredPlayersIds")

      val missingPlayers = retrieveMissingPlayers(notRegisteredPlayersIds)

      if (missingPlayers.toRegister.isNotEmpty())
        when (val result = persistPlayerService.persistAll(missingPlayers.toRegister)) {

          is PlayerPersistenceFailure ->
            players
                .filterNot { it.id in missingPlayers.toSearchIds }
                .let { persistPlayersPointsRepository.persistAll(it.toSet()) } // TODO Test failure (throws exception)
                .let { FantaPointPersistenceSucceedWithErrors(result.message) }

          is PlayerPersistenceSuccess -> players
              .filterNot { it.id in (missingPlayers.notFound ?: emptySet()) }
              .let { persistPlayersPointsRepository.persistAll(it.toSet()) } // TODO Test failure (throws exception)
              .let { FantaPointPersistenceSuccess }
        }
      else
        players
            .filterNot { it.id in missingPlayers.toSearchIds }
            .toSet()
            .let(::persistPlayersPoints)
    }
  }

  private fun persistPlayersPoints(playersScoresToPersist: Set<AtpPlayer>): FantaPointPersistence {

      persistPlayersPointsRepository.persistAll(playersScoresToPersist)

      return FantaPointPersistenceSuccess
  }

  private fun retrieveMissingPlayers(notRegisteredPlayersIds: Set<AtpPlayerId>): MissingPlayers =
      when (val rankedPlayers = rankingService.retrieveRankedPlayer(1000)) {

        is EmptyRanking -> MissingPlayers(toSearchIds = notRegisteredPlayersIds,
                                          toRegister = emptySet(),
                                          notFound = notRegisteredPlayersIds)
        is RankedPlayers -> {

          val rankedPlayersByAtpId = rankedPlayers.players
              .associateBy { it.id }

          val playersToRegister = notRegisteredPlayersIds
              .filter { rankedPlayersByAtpId[it] != null }
              .map { toDomain(rankedPlayersByAtpId[it]!!) }
              .toSet()

          val playersToRegisterIds = playersToRegister
              .map { it.id }

          val notFoundPlayersIds = notRegisteredPlayersIds
              .filterNot { it in playersToRegisterIds }
              .toSet()

          if (notFoundPlayersIds.isNotEmpty())
                MissingPlayers(toSearchIds = notRegisteredPlayersIds,
                               toRegister = playersToRegister,
                               notFound = notFoundPlayersIds)
                    .also { LOGGER.warn("Players $notFoundPlayersIds not present in top 1000") }
          else
            MissingPlayers(toSearchIds = notRegisteredPlayersIds, toRegister = playersToRegister)
        }
      }

  private fun toDomain(rankedPlayer: RankedPlayerDto): DomainPlayer =
      DomainPlayer(id = rankedPlayer.id,
                   atpId = rankedPlayer.id,
                   fullName = rankedPlayer.fullName)

  private data class MissingPlayers(val toSearchIds: Set<AtpPlayerId>,
                                    val toRegister: Set<DomainPlayer>,
                                    val notFound: Set<AtpPlayerId>? = null)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(FantaPointPersistenceService::class.java)
  }
}
