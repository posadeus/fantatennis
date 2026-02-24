package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao

import com.posadeus.fantatennis.domain.model.TeamId
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto

interface TeamDao {

  fun persist(teams: Set<JdbcTeamDto>)
  fun retrieveBy(teamIds: Set<TeamId>): List<JdbcTeamDto>
}