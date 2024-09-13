package com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.TeamsEntity
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.TeamsKeyEmbedded
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamsDao : CrudRepository<TeamsEntity, TeamsKeyEmbedded> {

  fun findByIdTeamId(teamId: Int): List<TeamsEntity>
}
