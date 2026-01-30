package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto

typealias FantaTeamId = Int

interface FantaTeamDao {

  fun retrieveBy(teamId: Int): JdbcFantaTeamDto
  fun persist(ownerId: String): FantaTeamId
}
