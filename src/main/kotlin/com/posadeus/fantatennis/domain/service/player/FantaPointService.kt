package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.exception.NoPointsForTournamentException
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import org.slf4j.LoggerFactory

class FantaPointService(private val fantaPointCalculatorService: FantaPointCalculatorService,
                        private val fantaPointPersistenceService: FantaPointPersistenceService,
                        private val playerService: PlayerService,
                        private val rankingService: RankingService) {

  fun playerFantaPointsFor(tournamentId: Int, year: Int) {

    fantaPointCalculatorService.calculateFantaPointsFor(tournamentId, year)
        .takeIf { it.isNotEmpty() }
        ?.let(::persist)
    ?: throw NoPointsForTournamentException()
        .also { LOGGER.warn("No Players found for tournament $tournamentId and year $year") }
  }

  private fun persist(playersScores: Set<AtpPlayer>) {

    val players = playerService.allPlayers()

    val notRegisteredPlayersAtpIds =
        playersScores
            .map { it.id }
            .filterNot { atpId -> atpId in players.map { it.atpId } }
            .toSet()

    if (notRegisteredPlayersAtpIds.isEmpty()) {

      fantaPointPersistenceService.persistScores(playersScores)
    }
    else {

      val rankedPlayers = rankingService.retrieveRankedPlayer(1000) as RankedPlayers
      val playersToRegister = findMissingPlayersInRankedPlayers(notRegisteredPlayersAtpIds, rankedPlayers)

      playerService.saveAll(playersToRegister)

      if (playersToRegister.size != notRegisteredPlayersAtpIds.size) {

        removeNotRegisteredPlayers(playersToRegister, notRegisteredPlayersAtpIds, playersScores)
            .let { fantaPointPersistenceService.persistScores(it) }
      }
      else {

        fantaPointPersistenceService.persistScores(playersScores)
      }
    }
  }

  private fun removeNotRegisteredPlayers(playersToRegister: Set<DomainPlayer>,
                                         notRegisteredPlayersAtpIds: Set<AtpPlayerId>,
                                         playersScores: Set<AtpPlayer>): Set<AtpPlayer> {

    val missingRankingPlayersIds = notRegisteredPlayersAtpIds.filter { id -> id !in playersToRegister.map { it.id } }

    LOGGER.warn("Players $missingRankingPlayersIds not present in top 1000")

    return playersScores
        .filter { playerScore -> playerScore.id !in missingRankingPlayersIds }
        .toSet()
  }

  private fun findMissingPlayersInRankedPlayers(missingPlayerAtpIds: Set<AtpPlayerId>,
                                                rankedPlayer: RankedPlayers): Set<DomainPlayer> =
      rankedPlayer
          .players
          .filter { it.id in missingPlayerAtpIds }
          .map { DomainPlayer(id = it.id, atpId = it.id, fullName = it.fullName) }
          .toSet()

  companion object {

    private val LOGGER = LoggerFactory.getLogger(FantaPointService::class.java)
  }
}
