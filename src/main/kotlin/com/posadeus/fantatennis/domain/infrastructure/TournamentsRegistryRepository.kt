package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.TournamentsRegistry

interface TournamentsRegistryRepository {

  fun retrieveAllTournamentsFor(year: Int): TournamentsRegistry
}
