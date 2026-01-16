package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.model.PlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import org.slf4j.LoggerFactory

class JdbcRetrievePlayersPointsRepository(private val playerPointsDao: PlayerPointsDao,
                                          private val playerDao: PlayerDao) : RetrievePlayersPointsRepository {

  override fun retrieveBy(tournamentId: Int): PlayersPoints {

    try {

      val playersPointsResult = playerPointsDao.retrieveByTournamentId(tournamentId)

      if (playersPointsResult.isEmpty()) return FoundPlayersPoints(playersPoints = emptyList())
      else {
        try {
          val allPlayers = playerDao.retrieveAll()
          return TODO()
        }
        catch (e: RuntimeException) {

          LOGGER.error(e.message)
          return InternalErrorPlayersPoints
        }
      }
    }
    catch (e: RuntimeException) {

      LOGGER.error(e.message)
      return InternalErrorPlayersPoints
    }
  }

  companion object {

    private val LOGGER = LoggerFactory.getLogger(JdbcRetrievePlayersPointsRepository::class.java)
  }
}