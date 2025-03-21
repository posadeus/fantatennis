package com.posadeus.fantatennis.domain.service.tournament

import com.posadeus.fantatennis.domain.infrastructure.TournamentsRepository
import com.posadeus.fantatennis.domain.model.Tournament

class RetrieveTournamentsService(private val tournamentsRepository: TournamentsRepository) {

  fun retrieveAll(): List<Tournament> =
      tournamentsRepository.getAllTournaments()
}
