package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FailureReason.EMPTY_RANKING
import com.posadeus.fantatennis.domain.model.FailureReason.MISSING_PLAYERS
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceFailure
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceSuccess
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import org.slf4j.LoggerFactory

class FantaPointPersistenceService(private val persistPlayersPointsRepository: PersistPlayersPointsRepository,
                                   private val retrievePlayerService: RetrievePlayerService,
                                   private val rankingService: RankingService) {

  fun persistScores(players: Set<AtpPlayer>) {

    if (players.isNotEmpty())
      persistPlayersPointsRepository.persistAll(players)
  }

  fun persist(players: Set<AtpPlayer>): FantaPointPersistence {

    val allPlayers = retrievePlayerService.allPlayers()
        .groupBy { it.atpId }

    return players
               .takeIf { it.size == allPlayers.size }
               ?.let { FantaPointPersistenceSuccess }
           ?: players
               .filterNot { it.id in allPlayers.keys }
               .let(::retrieveMissingPlayers)
  }

  private fun retrieveMissingPlayers(missingPlayers: List<AtpPlayer>): FantaPointPersistence =
      when (val rankedPlayers = rankingService.retrieveRankedPlayer(1000)) {

        is EmptyRanking -> FantaPointPersistenceFailure(reason = EMPTY_RANKING)
        is RankedPlayers -> {

          val notRegisteredPlayers = missingPlayers.filter { it.id in rankedPlayers.players.map { rankedPlayer -> rankedPlayer.id } }

          notRegisteredPlayers
              .takeIf { it.size == missingPlayers.size }
              ?.let { FantaPointPersistenceSuccess }
          ?: (missingPlayers subtract notRegisteredPlayers.toSet())
              .map { it.id }
              .let { missingRankingPlayersIds ->
                FantaPointPersistenceFailure(reason = MISSING_PLAYERS)
                    .also { LOGGER.warn("Players $missingRankingPlayersIds not present in top 1000") }
              }
        }
      }

  companion object {

    private val LOGGER = LoggerFactory.getLogger(FantaPointPersistenceService::class.java)
  }
}
