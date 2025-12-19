package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.model.PlayerPoints

class JdbcRetrievePlayersPointsRepository : RetrievePlayersPointsRepository {

  override fun retrieveBy(tournamentId: Int): List<PlayerPoints> {
    TODO("Not yet implemented")
  }
}