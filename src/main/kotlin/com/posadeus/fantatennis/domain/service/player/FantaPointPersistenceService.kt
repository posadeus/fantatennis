package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FailureReason.EMPTY_RANKING
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceFailure
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceSuccess
import com.posadeus.fantatennis.domain.service.ranking.RankingService

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
           ?: retrieveMissingPlayers()
  }

  private fun retrieveMissingPlayers(): FantaPointPersistence =
      when (val rankedPlayers = rankingService.retrieveRankedPlayer(1000)) {

        is EmptyRanking -> FantaPointPersistenceFailure(reason = EMPTY_RANKING)
        is RankedPlayers -> FantaPointPersistenceSuccess
      }
}
