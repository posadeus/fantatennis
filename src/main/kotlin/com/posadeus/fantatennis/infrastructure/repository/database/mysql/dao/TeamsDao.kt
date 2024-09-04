package com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.TeamKeyEmbedded
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.TeamsEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamsDao : CrudRepository<TeamsEntity, TeamKeyEmbedded> {

  fun findByIdTeamId(teamId: String): List<TeamsEntity>
}
