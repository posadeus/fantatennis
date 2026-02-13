package com.posadeus.fantatennis.domain.service.tournament

import com.posadeus.fantatennis.domain.infrastructure.PersistTournamentsRepository
import com.posadeus.fantatennis.domain.infrastructure.TournamentsRegistryRepository
import com.posadeus.fantatennis.domain.model.TournamentsCreated.ErrorTournamentsCreation
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.FoundTournamentsRegistry
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.NotFoundTournamentsRegistry

class AddTournamentsService(private val tournamentsRegistryRepository: TournamentsRegistryRepository,
                            private val persistTournamentsRepository: PersistTournamentsRepository) {

  fun addTournamentsFor(year: Int) =
      when (val tournamentsRegistry = tournamentsRegistryRepository.retrieveAllTournamentsFor(year)) {

        is FoundTournamentsRegistry -> persistTournamentsRepository.persistNewTournaments(tournamentsRegistry)
        is NotFoundTournamentsRegistry -> ErrorTournamentsCreation("No Tournament found for year $year")
      }
}
