package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTeamsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTeamsEntity

class MySqlFantaTeamsRepository(private val fantaTeamsDao: FantaTeamsDao) : FantaTeamsRepository {

  override fun createTeam(ownerId: String): FantaTeam =
      try {
        fantaTeamsDao.save(FantaTeamsEntity(ownerId = ownerId))
            .let { FantaTeamOk(id = it.teamId, ownerId = it.ownerId) }
      }
      catch (e: Exception) {

        FantaTeamError
      }
}
