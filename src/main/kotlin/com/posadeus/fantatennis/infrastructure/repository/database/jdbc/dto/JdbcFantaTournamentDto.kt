package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import org.springframework.jdbc.core.RowMapper

data class JdbcFantaTournamentDto(val id: Int,
                                  val startingTournamentId: Int,
                                  val endingTournamentId: Int,
                                  val year: Int) {

  companion object {

    val fantaTournamentRowMapper = RowMapper { rs, _ ->
      JdbcFantaTournamentDto(id = rs.getInt("FANTA_TOURNAMENT_ID"),
                             startingTournamentId = rs.getInt("STARTING_TOURNAMENT"),
                             endingTournamentId = rs.getInt("ENDING_TOURNAMENT"),
                             year = rs.getInt("TOURNAMENT_YEAR"))
    }
  }
}
