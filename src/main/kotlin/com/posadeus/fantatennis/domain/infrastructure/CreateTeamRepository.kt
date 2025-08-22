package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.FantaTeam

interface CreateTeamRepository {

  fun create(ownerId: String, fantaTournamentId: Int): FantaTeam
}
