package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.FantaTournament

interface RetrieveFantaTournamentRepository {

  fun retrieve(fantaTournamentId: Int): FantaTournament
}
