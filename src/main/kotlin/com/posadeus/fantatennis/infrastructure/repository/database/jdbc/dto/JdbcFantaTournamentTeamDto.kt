package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import org.springframework.jdbc.core.RowMapper

data class JdbcFantaTournamentTeamDto(val teamId: Int,
                                      val fantaTournamentId: Int) {

  companion object {

    val fantaTournamentTeamRowMapper = RowMapper { rs, _ ->
      JdbcFantaTournamentTeamDto(teamId = rs.getInt("TEAM_ID"),
                                 fantaTournamentId = rs.getInt("FANTA_TOURNAMENT_ID"))
    }
  }
}
