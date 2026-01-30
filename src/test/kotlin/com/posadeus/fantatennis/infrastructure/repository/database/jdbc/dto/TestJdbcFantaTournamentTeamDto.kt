package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

object TestJdbcFantaTournamentTeamDto {

  fun aJdbcFantaTournamentTeamDto(teamId: Int = 0,
                                  fantaTournamentId: Int = 0): JdbcFantaTournamentTeamDto =
      JdbcFantaTournamentTeamDto(teamId = teamId,
                                 fantaTournamentId = fantaTournamentId)
}