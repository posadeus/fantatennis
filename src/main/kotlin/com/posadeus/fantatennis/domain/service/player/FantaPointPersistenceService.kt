package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.controller.model.ranking.RankedPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FailureReason.PERSISTENCE_ERROR
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceFailure
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

    val allPlayers = retrievePlayerService.allPlayers()
        .groupBy { it.atpId }

    return players
               .takeIf { it.size == allPlayers.size }
               ?.let { persistPlayersPointsRepository.persistAll(it) }
               ?.let { FantaPointPersistenceSuccess }
           ?: persistPoints(players, allPlayers)
  }

  private fun persistPoints(players: Set<AtpPlayer>, allPlayers: Map<String, List<DomainPlayer>>): FantaPointPersistence {

    val playersToSearch = players.filterNot { it.id in allPlayers.keys }
    val missingPlayers = playersToSearch.let(::retrieveMissingPlayers)

    if (missingPlayers.notRegistered.isEmpty()) {

      val playersScoresToPersist = players subtract playersToSearch.toSet()

      persistPlayersPointsRepository.persistAll(playersScoresToPersist)

      return FantaPointPersistenceSuccess
    }
    else {

      when (persistPlayerService.persistAll(missingPlayers.notRegistered)) {

        is PlayerPersistenceFailure -> return FantaPointPersistenceFailure(reason = PERSISTENCE_ERROR)
        is PlayerPersistenceSuccess -> {

          players
              .filter { it.id !in (missingPlayers.notFound ?: emptySet()) }
              .let { persistPlayersPointsRepository.persistAll(it.toSet()) } // TODO Test failure (throws exception)

          return FantaPointPersistenceSuccess
        }
      }
    }
  }

  private fun retrieveMissingPlayers(missingPlayers: List<AtpPlayer>): MissingPlayers =
      when (val rankedPlayers = rankingService.retrieveRankedPlayer(1000)) {

        is EmptyRanking -> MissingPlayers(emptySet(), missingPlayers.map { it.id }.toSet())
        is RankedPlayers -> {

          val rankedPlayersByAtpId = rankedPlayers.players.associateBy { it.id }

          val notRegisteredPlayers = missingPlayers
              .filter { rankedPlayersByAtpId[it.id] != null }
              .map { toDomain(rankedPlayersByAtpId[it.id]!!) }
              .toSet()

          (missingPlayers.map { it.id } subtract notRegisteredPlayers.map { it.id }.toSet())
              .takeIf { it.isNotEmpty() }
              ?.let { missingRankingPlayersIds ->
                MissingPlayers(notRegistered = notRegisteredPlayers, notFound = missingRankingPlayersIds)
                    .also { LOGGER.warn("Players $missingRankingPlayersIds not present in top 1000") }
              }
          ?: MissingPlayers(notRegistered = notRegisteredPlayers)
        }
      }

  private fun toDomain(rankedPlayer: RankedPlayerDto): DomainPlayer =
      DomainPlayer(id = rankedPlayer.id,
                   atpId = rankedPlayer.id,
                   fullName = rankedPlayer.fullName)

  private data class MissingPlayers(val notRegistered: Set<DomainPlayer>,
                                    val notFound: Set<AtpPlayerId>? = null)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(FantaPointPersistenceService::class.java)
  }
}
