package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

object TestJdbcTeamDto {

  fun aJdbcTeamDto(teamId: Int = 0,
                   playerId: String = "A_PLAYER_ID",
                   startingTournamentId: Int = 1,
                   endingTournamentId: Int = 2): JdbcTeamDto =
      JdbcTeamDto(teamId = teamId,
                  playerId = playerId,
                  startingTournamentId = startingTournamentId,
                  endingTournamentId = endingTournamentId)
}