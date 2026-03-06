package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.DomainTeam
import com.posadeus.fantatennis.domain.model.Team

interface RetrieveFantaTeamRepository {

  fun retrieve(teamId: Int): Team
  fun retrieveByTeamId(teamId: Int): DomainTeam
}
