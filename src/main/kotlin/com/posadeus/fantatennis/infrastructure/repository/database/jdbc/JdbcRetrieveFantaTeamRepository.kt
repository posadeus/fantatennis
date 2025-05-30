package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.Team

// TODO create the Bean
class JdbcRetrieveFantaTeamRepository : RetrieveFantaTeamRepository {

  override fun retrieve(teamId: Int): Team {
    TODO("Not yet implemented")
  }
}