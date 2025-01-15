package com.posadeus.fantatennis.domain.service.tournament

import com.posadeus.fantatennis.controller.model.tournament.TournamentsDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament

class RetrieveTournamentsService(private val fantaTournamentsRepository: FantaTournamentsRepository) {

  fun retrieveAll(): FantaTournamentsResults =
      fantaTournamentsRepository.retrieveAll()
          .takeIf { it.isNotEmpty() }
          ?.map {
            when (it) {
              is InvalidFantaTournament -> return ErrorFantaTournamentsResults
              is ValidFantaTournament -> it.id
            }
          }
          ?.let(::TournamentsDto)
          ?.let(::FoundFantaTournamentsResults)
      ?: NotFoundFantaTournaments
}
