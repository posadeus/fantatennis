package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import org.springframework.jdbc.core.RowMapper

data class JdbcPlayerPointsDto(val tournamentYear: Int,
                               val tournamentId: Int,
                               val playerId: String,
                               val fantaPoints: Double) {

  companion object {

    val playersPointsRowMapper = RowMapper { rs, _ ->
      JdbcPlayerPointsDto(playerId = rs.getString("PLAYER_ID"),
                          tournamentYear = rs.getInt("TOURNAMENT_YEAR"),
                          tournamentId = rs.getInt("TOURNAMENT_ID"),
                          fantaPoints = rs.getDouble("FANTA_POINTS"))
    }
  }
}

