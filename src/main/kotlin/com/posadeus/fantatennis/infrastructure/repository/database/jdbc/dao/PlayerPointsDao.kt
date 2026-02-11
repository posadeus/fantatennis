package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto

interface PlayerPointsDao {

  fun retrieveByTournamentId(tournamentId: Int): List<JdbcPlayerPointsDto>
  fun persistAll(players: List<JdbcPlayerPointsDto>)
}
