package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.FantaTournaments

interface RetrieveAllFantaTournamentsRepository {

  fun retrieve(): FantaTournaments
}
