package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import org.springframework.jdbc.core.RowMapper

data class JdbcTeamDto(val teamId: Int,
                       val playerId: String,
                       val startingTournamentId: Int,
                       val endingTournamentId: Int?) {

  companion object {

    val teamRowMapper = RowMapper { rs, _ ->
      JdbcTeamDto(teamId = rs.getInt("TEAM_ID"),
                  playerId = rs.getString("PLAYER_ID"),
                  startingTournamentId = rs.getInt("STARTING_TOURNAMENT"),
                  endingTournamentId = rs.getObject("ENDING_TOURNAMENT", Integer::class.java)?.toInt())
    }
  }
}
