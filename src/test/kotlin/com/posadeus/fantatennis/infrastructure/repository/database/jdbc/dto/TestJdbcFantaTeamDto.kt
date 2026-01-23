package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

object TestJdbcFantaTeamDto {

  fun aJdbcFantaTeamDto(teamId: Int = 0,
                        ownerId: String = "AN_OWNER_ID"): JdbcFantaTeamDto =
      JdbcFantaTeamDto(teamId = teamId,
                       ownerId = ownerId)
}