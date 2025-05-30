package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

object TestJdbcFantaTournamentDto {

  fun aJdbcFantaTournamentDto(id: Int = 1,
                              startingTournamentId: Int = 123,
                              endingTournamentId: Int = 456,
                              year: Int = 2025): JdbcFantaTournamentDto =
      JdbcFantaTournamentDto(id = id,
                             startingTournamentId = startingTournamentId,
                             endingTournamentId = endingTournamentId,
                             year = year)
}