package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.TournamentsRegistry

interface TournamentRegistryRepository {

  fun retrieveAllTournamentsFor(year: Int): TournamentsRegistry
}
