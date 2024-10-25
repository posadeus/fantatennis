package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.FantaTeam

interface FantaTeamsRepository {

  fun createTeam(ownerId: String, tournamentId: Int?): FantaTeam
}
