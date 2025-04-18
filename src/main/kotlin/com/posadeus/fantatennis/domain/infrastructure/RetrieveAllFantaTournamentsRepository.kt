package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.FantaTournament

interface RetrieveAllFantaTournamentsRepository {

  fun retrieve(): Set<FantaTournament>
}
