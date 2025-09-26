package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import org.springframework.jdbc.core.RowMapper

data class JdbcFantaTeamResultsDto(val playerId: String,
                                   val playerFullName: String,
                                   val playerTotalScore: Double) {

  companion object {

    val fantaTeamResultsRowMapper = RowMapper { rs, _ ->
      JdbcFantaTeamResultsDto(playerId = rs.getString("PLAYER_ID"),
                              playerFullName = rs.getString("FULL_NAME"),
                              playerTotalScore = rs.getDouble("TOTAL_SCORE"))
    }
  }
}
