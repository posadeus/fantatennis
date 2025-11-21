package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.exception.MissingPlayersPersistenceException
import com.posadeus.fantatennis.domain.exception.NoPointsForTournamentException
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FailureReason.NO_POINTS_FOR_TOURNAMENT
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceFailure
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceFailure
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceSuccess
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import org.slf4j.LoggerFactory

class FantaPointService(private val fantaPointCalculatorService: FantaPointCalculatorService,
                        private val fantaPointPersistenceService: FantaPointPersistenceService,
                        private val retrievePlayerService: RetrievePlayerService,
                        private val persistPlayerService: PersistPlayerService,
                        private val rankingService: RankingService) {

  fun updateFantaPointsFor(tournamentId: Int, year: Int): FantaPointPersistence =
      fantaPointCalculatorService.calculateFantaPointsFor(tournamentId, year)
          .takeIf(Set<AtpPlayer>::isNotEmpty)
          ?.let(fantaPointPersistenceService::persist)
      ?: FantaPointPersistenceFailure(NO_POINTS_FOR_TOURNAMENT)




  @Deprecated("Use updateFantaPointsFor")
  fun playerFantaPointsFor(tournamentId: Int, year: Int) {

    fantaPointCalculatorService.calculateFantaPointsFor(tournamentId, year)
        .takeIf { it.isNotEmpty() }
        ?.let(::persist)
    ?: throw NoPointsForTournamentException()
        .also { LOGGER.warn("No Players found for tournament $tournamentId and year $year") }
  }

  private fun persist(playersScores: Set<AtpPlayer>) {

    val players = retrievePlayerService.allPlayers()

    val notRegisteredPlayersAtpIds =
        playersScores
            .map { it.id }
            .filterNot { atpId -> atpId in players.map { it.atpId } }
            .toSet()

    if (notRegisteredPlayersAtpIds.isEmpty()) {

      fantaPointPersistenceService.persistScores(playersScores)
    }
    else {

      LOGGER.warn("Missing players: $notRegisteredPlayersAtpIds")

      val rankedPlayers = rankingService.retrieveRankedPlayer(1000) as RankedPlayers
      val playersToRegister = findMissingPlayersInRankedPlayers(notRegisteredPlayersAtpIds, rankedPlayers)

      when (val playerPersistence = persistPlayerService.persistAll(playersToRegister)) {

        is PlayerPersistenceFailure -> throw MissingPlayersPersistenceException(playerPersistence.message)
        is PlayerPersistenceSuccess -> persist(playersToRegister, notRegisteredPlayersAtpIds, playersScores)
      }
    }
  }

  private fun persist(playersToRegister: Set<DomainPlayer>,
                      notRegisteredPlayersAtpIds: Set<AtpPlayerId>,
                      playersScores: Set<AtpPlayer>) {

    if (playersToRegister.size != notRegisteredPlayersAtpIds.size) {

      removeNotRegisteredPlayers(playersToRegister, notRegisteredPlayersAtpIds, playersScores)
          .let { fantaPointPersistenceService.persistScores(it) }
    }
    else {

      fantaPointPersistenceService.persistScores(playersScores)
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
