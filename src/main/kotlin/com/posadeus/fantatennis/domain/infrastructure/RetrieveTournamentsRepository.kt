package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.Tournament
import com.posadeus.fantatennis.domain.model.Tournament.FoundTournament

interface RetrieveTournamentsRepository {

  fun retrieveAllBy(year: Int): List<FoundTournament>
  fun retrieveBy(tournamentId: Int): Tournament
}
