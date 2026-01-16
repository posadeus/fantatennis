package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

object TestJdbcPlayerPointsDto {

  fun aJdbcPlayerPointsDto(tournamentYear: Int = 2026,
                           tournamentId: Int = 123,
                           playerId: String = "A_PLAYER_ID",
                           fantaPoints: Double = 0.0) =
      JdbcPlayerPointsDto(tournamentYear = tournamentYear,
                          tournamentId = tournamentId,
                          playerId = playerId,
                          fantaPoints = fantaPoints)
}