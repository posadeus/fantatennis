package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import org.springframework.jdbc.core.RowMapper

data class JdbcFantaTournamentsTeamsDto(val teamId: Int,
                                        val startingTournamentId: Int,
                                        val endingTournamentId: Int,
                                        val tournamentYear: Int,
                                        val ownerId: String) {

  companion object {

    val fantaTournamentsTeamsRowMapper = RowMapper { rs, _ ->
      JdbcFantaTournamentsTeamsDto(teamId = rs.getInt("TEAM_ID"),
                                   startingTournamentId = rs.getInt("STARTING_TOURNAMENT"),
                                   endingTournamentId = rs.getInt("ENDING_TOURNAMENT"),
                                   tournamentYear = rs.getInt("TOURNAMENT_YEAR"),
                                   ownerId = rs.getString("OWNER_ID"))
    }
  }
}