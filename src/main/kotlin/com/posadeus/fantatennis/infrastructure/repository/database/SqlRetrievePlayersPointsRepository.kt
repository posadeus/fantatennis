package com.posadeus.fantatennis.infrastructure.repository.database

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.model.PlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints.PlayerPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto
import org.slf4j.LoggerFactory

class SqlRetrievePlayersPointsRepository(private val playerPointsDao: PlayerPointsDao,
                                         private val playerDao: PlayerDao) : RetrievePlayersPointsRepository {

  override fun retrieveByTournamentId(tournamentId: Int): PlayersPoints =
      retrieve { playerPointsDao.retrieveByTournamentId(tournamentId) }

  override fun retrieveByYear(year: Int): PlayersPoints =
      retrieve { playerPointsDao.retrieveByTournamentYear(year) }

  private fun retrieve(retrieveFromDao: () -> List<JdbcPlayerPointsDto>): PlayersPoints =
      try {

        val playerPointsByPlayerId = retrieveFromDao().groupBy { it.playerId }

        playerDao.retrieveAll()
            .map { player ->
              playerPointsByPlayerId[player.playerId]
                  ?.let { toPlayerPoints(player.playerId, player.fullName, it) }
                  ?: PlayerPoints(playerId = player.playerId,
                                  playerName = player.fullName,
                                  pointsByTournament = emptyMap(),
                                  totalPoints = 0.0)
            }
            .let(::FoundPlayersPoints)
      }
      catch (e: RuntimeException) {

        LOGGER.error(e.message)
        InternalErrorPlayersPoints
      }

  private fun toPlayerPoints(playerId: String,
                             playerFullName: String,
                             playerPoints: List<JdbcPlayerPointsDto>): PlayerPoints =
      PlayerPoints(playerId = playerId,
                   playerName = playerFullName,
                   pointsByTournament = playerPoints.associate { it.tournamentId to it.fantaPoints },
                   totalPoints = playerPoints.sumOf { it.fantaPoints })

  companion object {

    private val LOGGER = LoggerFactory.getLogger(SqlRetrievePlayersPointsRepository::class.java)
  }
}
