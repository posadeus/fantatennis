package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import org.springframework.jdbc.core.RowMapper

data class JdbcTeamPlayerPointsDto(val playerId: String,
                                   val playerName: String,
                                   val tournamentId: Int,
                                   val tournamentScore: Double = 0.00) {

  companion object {

    val teamPlayerPointsRowMapper = RowMapper { rs, _ ->
      JdbcTeamPlayerPointsDto(playerId = rs.getString("PLAYER_ID"),
                              playerName = rs.getString("FULL_NAME"),
                              tournamentId = rs.getInt("TOURNAMENT_ID"),
                              tournamentScore = rs.getDouble("FANTA_POINTS"))
    }
  }
}