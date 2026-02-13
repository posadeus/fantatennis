package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto

interface TournamentDao {

  fun retrieveAllBy(year: Int): List<JdbcTournamentDto>
  fun retrieveBy(id: Int): JdbcTournamentDto
  fun persistNewTournaments(tournaments: List<JdbcTournamentDto>)
}
