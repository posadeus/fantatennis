package com.posadeus.fantatennis.domain.service.tournament

import com.posadeus.fantatennis.domain.exception.InvalidYearException
import com.posadeus.fantatennis.domain.infrastructure.TournamentsRegistryRepository
import com.posadeus.fantatennis.domain.infrastructure.TournamentsRepository
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.FoundTournamentsRegistry
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.NotFoundTournamentsRegistry

class AddTournamentsService(private val tournamentsRegistryRepository: TournamentsRegistryRepository,
                            private val tournamentsRepository: TournamentsRepository) {

  fun addTournamentsFor(year: Int) {

    when (val tournamentsRegistry = tournamentsRegistryRepository.retrieveAllTournamentsFor(year)) {

      is FoundTournamentsRegistry -> tournamentsRepository.persist(tournamentsRegistry)
      is NotFoundTournamentsRegistry -> throw InvalidYearException(year.toString())
    }
  }
}
