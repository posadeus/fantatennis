package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto

interface FantaTournamentDao {

  fun retrieveBy(fantaTournamentId: Int): JdbcFantaTournamentDto
  fun retrieveAll(): List<JdbcFantaTournamentDto>
}
