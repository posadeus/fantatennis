package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.model.PlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import org.slf4j.LoggerFactory

class JdbcRetrievePlayersPointsRepository(private val playerPointsDao: PlayerPointsDao) : RetrievePlayersPointsRepository {

  override fun retrieveBy(tournamentId: Int): PlayersPoints {

    try {

      if (playerPointsDao.retrieveByTournamentId(tournamentId).isEmpty()) return FoundPlayersPoints(emptyList())
      else TODO()
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