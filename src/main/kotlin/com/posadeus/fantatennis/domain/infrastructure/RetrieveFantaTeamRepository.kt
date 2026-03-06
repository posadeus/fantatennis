package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.*

interface RetrieveFantaTeamRepository {

  fun retrieve(teamId: Int): Team
  fun retrieveByTeamId(id: TeamId): DomainTeam
  fun retrieveByFantaTournamentId(id: TournamentId): Teams
}
