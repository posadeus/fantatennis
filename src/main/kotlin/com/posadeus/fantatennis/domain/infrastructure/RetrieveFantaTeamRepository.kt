package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.*

interface RetrieveFantaTeamRepository {

  @Deprecated("Use retrieveByTeamId; points calculation must move to the service layer before callers can migrate.")
  fun retrieve(teamId: Int): Team
  fun retrieveByTeamId(id: TeamId): DomainTeam
  fun retrieveByFantaTournamentId(id: TournamentId): Teams
}
