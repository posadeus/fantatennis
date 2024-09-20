package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.domain.model.RankedPlayers
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import org.slf4j.LoggerFactory

class FantaPointService(private val fantaPointCalculatorService: FantaPointCalculatorService,
                        private val fantaPointPersistenceService: FantaPointPersistenceService,
                        private val playerService: PlayerService,
                        private val rankingService: RankingService) {

  fun playerFantaPointsFor(tournamentId: Int, year: Int) {

    val tournamentPlayers = fantaPointCalculatorService.calculateFantaPointsFor(tournamentId, year)
    val players = playerService.allPlayers()

    if (players.map { it.atpId }.containsAll(tournamentPlayers.map { it.id })) {

      fantaPointPersistenceService.persistScores(tournamentPlayers)
    }
    else {

      val missingPlayerAtpIds = tournamentPlayers
          .map { it.id }
          .filterNot { atpId -> atpId in players.map { it.atpId } }

      val rankedPlayer = rankingService.retrieveRankedPlayer(1000)

      val missingDomainPlayers = (rankedPlayer as RankedPlayers)
          .players
          .filter { it.id in missingPlayerAtpIds }
          .map { DomainPlayer(id = it.id,
                              atpId = it.id,
                              fullName = it.fullName)
          }
          .toSet()

      if (missingDomainPlayers.size == missingPlayerAtpIds.size) {

        fantaPointPersistenceService.persistPlayersAndScores(missingDomainPlayers, tournamentPlayers)
      }
      else {

        val missingRankingPlayersIds = tournamentPlayers
            .filterNot { tournamentPlayer -> tournamentPlayer.id in rankedPlayer.players.map { it.id } }
            .map { it.id }
            .toSet()

        LOGGER.warn("Players $missingRankingPlayersIds not present in top 1000")

        val playerScoresToUpdate = tournamentPlayers
            .filter { tournamentPlayer -> tournamentPlayer.id in rankedPlayer.players.map { it.id } }
            .toSet()

        fantaPointPersistenceService.persistPlayersAndScores(missingDomainPlayers, playerScoresToUpdate)
      }
    }
  }

  companion object {

    private val LOGGER = LoggerFactory.getLogger(FantaPointService::class.java)
  }
}
