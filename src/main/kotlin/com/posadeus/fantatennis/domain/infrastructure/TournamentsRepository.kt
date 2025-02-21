package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.Tournament
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.FoundTournamentsRegistry

interface TournamentsRepository {

  fun readTournaments(year: Int): List<Tournament>
  fun persist(tournamentsRegistry: FoundTournamentsRegistry)
}
