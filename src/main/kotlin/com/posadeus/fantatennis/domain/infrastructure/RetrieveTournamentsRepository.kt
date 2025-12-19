package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.Tournament

interface RetrieveTournamentsRepository {

  fun retrieveAllBy(year: Int): List<Tournament>
  fun retrieveBy(tournamentId: Int): Tournament
}
