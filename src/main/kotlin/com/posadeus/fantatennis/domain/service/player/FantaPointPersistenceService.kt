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

    val allPlayersByAtpId = retrievePlayerService.allPlayers()
        .groupBy { it.atpId }

    return players
               .takeIf { areAllPlayerAlreadyRegistered(it, allPlayersByAtpId) }
               ?.let { persistPlayersPointsRepository.persistAll(it) }
               ?.let { FantaPointPersistenceSuccess }
           ?: run {

             val missingPlayers = retrieveMissingPlayers(players, allPlayersByAtpId)

             if (areMissingPlayersFound(missingPlayers))
               when (persistPlayerService.persistAll(missingPlayers.notRegistered)) {

                 is PlayerPersistenceFailure -> FantaPointPersistenceFailure(reason = PERSISTENCE_ERROR)
                 is PlayerPersistenceSuccess -> players
                     .filter { it.id !in (missingPlayers.notFound ?: emptySet()) }
                     .let { persistPlayersPointsRepository.persistAll(it.toSet()) } // TODO Test failure (throws exception)
                     .let { FantaPointPersistenceSuccess }
               }
             else
               persistPlayersPoints(players subtract missingPlayers.playersToSearch.toSet())
           }
  }

  private fun areMissingPlayersFound(missingPlayers: MissingPlayers) =
      missingPlayers.notRegistered.isNotEmpty()

  private fun areAllPlayerAlreadyRegistered(players: Set<AtpPlayer>, allPlayersByAtpId: Map<String, List<DomainPlayer>>) =
      players.all { it.id in allPlayersByAtpId.keys }

  private fun persistPlayersPoints(playersScoresToPersist: Set<AtpPlayer>): FantaPointPersistence {

      persistPlayersPointsRepository.persistAll(playersScoresToPersist)

      return FantaPointPersistenceSuccess
  }

  private fun retrieveMissingPlayers(players: Set<AtpPlayer>, allPlayers: Map<String, List<DomainPlayer>>): MissingPlayers =
      players
          .filterNot { it.id in allPlayers.keys }
          .let(this::retrieveMissingPlayers)

  private fun retrieveMissingPlayers(missingPlayers: List<AtpPlayer>): MissingPlayers =
      when (val rankedPlayers = rankingService.retrieveRankedPlayer(1000)) {

        is EmptyRanking -> MissingPlayers(playersToSearch = missingPlayers,
                                          notRegistered = emptySet(),
                                          notFound = missingPlayers.map { it.id }.toSet())
        is RankedPlayers -> {

          val rankedPlayersByAtpId = rankedPlayers.players.associateBy { it.id }

          val notRegisteredPlayers = missingPlayers
              .filter { rankedPlayersByAtpId[it.id] != null }
              .map { toDomain(rankedPlayersByAtpId[it.id]!!) }
              .toSet()

          (missingPlayers.map { it.id } subtract notRegisteredPlayers.map { it.id }.toSet())
              .takeIf { it.isNotEmpty() }
              ?.let { missingRankingPlayersIds ->
                MissingPlayers(playersToSearch = missingPlayers, notRegistered = notRegisteredPlayers, notFound = missingRankingPlayersIds)
                    .also { LOGGER.warn("Players $missingRankingPlayersIds not present in top 1000") }
              }
          ?: MissingPlayers(playersToSearch = missingPlayers, notRegistered = notRegisteredPlayers)
        }
      }

  private fun toDomain(rankedPlayer: RankedPlayerDto): DomainPlayer =
      DomainPlayer(id = rankedPlayer.id,
                   atpId = rankedPlayer.id,
                   fullName = rankedPlayer.fullName)

  private data class MissingPlayers(val playersToSearch: List<AtpPlayer>,
                                    val notRegistered: Set<DomainPlayer>,
                                    val notFound: Set<AtpPlayerId>? = null)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(FantaPointPersistenceService::class.java)
  }
}
