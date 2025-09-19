package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import org.springframework.jdbc.core.RowMapper

data class JdbcTournamentResultsDto(val teamId: Int,
                                    val ownerId: String,
                                    val playerId: String,
                                    val playerFullName: String,
                                    val playerTotalScore: Double) {

  companion object {

    val tournamentResultsRowMapper = RowMapper { rs, _ ->
      JdbcTournamentResultsDto(teamId = rs.getInt("TEAM_ID"),
                               ownerId = rs.getString("OWNER_ID"),
                               playerId = rs.getString("PLAYER_ID"),
                               playerFullName = rs.getString("FULL_NAME"),
                               playerTotalScore = rs.getDouble("TOTAL_SCORE"))
    }
  }
}