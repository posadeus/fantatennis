package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.model.PlayerPoints
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao

class JdbcRetrievePlayersPointsRepository(private val playerPointsDao: PlayerPointsDao) : RetrievePlayersPointsRepository {

  override fun retrieveBy(tournamentId: Int): List<PlayerPoints> {

    if (playerPointsDao.retrieveByTournamentId(tournamentId).isEmpty()) return emptyList()
    else TODO()
  }
}