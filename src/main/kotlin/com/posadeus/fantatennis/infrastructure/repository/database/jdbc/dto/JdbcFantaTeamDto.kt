package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import org.springframework.jdbc.core.RowMapper

data class JdbcFantaTeamDto(val teamId: Int,
                            val ownerId: String) {

  companion object {

    val fantaTeamRowMapper = RowMapper { rs, _ ->
      JdbcFantaTeamDto(teamId = rs.getInt("TEAM_ID"),
                       ownerId = rs.getString("OWNER_ID"))
    }
  }
}
