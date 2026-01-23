package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto

interface FantaTeamDao {

  fun retrieveBy(teamId: Int): JdbcFantaTeamDto
}
