package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.FantaTeam
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament

interface FantaTeamsRepository {

  fun createTeam(ownerId: String, validFantaTournament: ValidFantaTournament): FantaTeam
}
