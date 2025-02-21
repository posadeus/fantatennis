package com.posadeus.fantatennis.domain.service.tournament

import com.posadeus.fantatennis.domain.infrastructure.TournamentRegistryRepository
import com.posadeus.fantatennis.domain.infrastructure.TournamentsRepository
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.FoundTournamentsRegistry

class AddTournamentsService(private val tournamentRegistryRepository: TournamentRegistryRepository,
                            private val tournamentsRepository: TournamentsRepository) {

  fun addTournamentsFor(year: Int) {

    val tournamentsRegistry = tournamentRegistryRepository.retrieveAllTournamentsFor(year)
    tournamentsRepository.persist(tournamentsRegistry as FoundTournamentsRegistry)
  }
}
