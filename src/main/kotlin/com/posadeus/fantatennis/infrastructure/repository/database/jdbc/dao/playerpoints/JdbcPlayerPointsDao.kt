package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto

class JdbcPlayerPointsDao : PlayerPointsDao {

  override fun retrieveByTournamentId(tournamentId: Int): List<JdbcPlayerPointsDto> {
    TODO("Not yet implemented")
  }
}
