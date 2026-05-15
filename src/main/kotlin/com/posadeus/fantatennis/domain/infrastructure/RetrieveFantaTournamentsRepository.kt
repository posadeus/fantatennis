package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.FantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournaments

interface RetrieveFantaTournamentsRepository {

  fun retrieve(): FantaTournaments
  fun retrieveBy(fantaTournamentId: Int): FantaTournament
}
