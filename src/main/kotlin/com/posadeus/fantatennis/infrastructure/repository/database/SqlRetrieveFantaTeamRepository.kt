package com.posadeus.fantatennis.infrastructure.repository.database

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.DomainTeam
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.domain.model.Team
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.*
import org.springframework.dao.EmptyResultDataAccessException

class SqlRetrieveFantaTeamRepository(private val teamDao: TeamDao,
                                     private val fantaTeamDao: FantaTeamDao,
                                     private val fantaTournamentTeamDao: FantaTournamentTeamDao) : RetrieveFantaTeamRepository {

  override fun retrieve(teamId: Int): Team {
    TODO("Not yet implemented")
  }

  override fun retrieveByTeamId(teamId: Int): DomainTeam {

    try {

      fantaTeamDao.retrieveBy(teamId)

      if (teamDao.retrieveBy(setOf(teamId)).isEmpty()) return NotFoundDomainTeam
    }
    catch (_: EmptyResultDataAccessException) {

      return NotFoundDomainTeam
    }

    return TODO()
  }
}
