package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.TeamEntity
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.TeamKeyEmbedded
import org.springframework.data.repository.CrudRepository

interface MySqlTeamDao : CrudRepository<TeamEntity, TeamKeyEmbedded> {

  fun findByIdTeamId(teamId: String): List<TeamEntity>
}
