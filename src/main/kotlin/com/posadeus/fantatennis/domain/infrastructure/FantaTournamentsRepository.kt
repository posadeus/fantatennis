package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.FantaTournament

interface FantaTournamentsRepository {

  fun retrieve(tournamentId: Int): FantaTournament
  fun retrieveAll(): List<FantaTournament>
}
