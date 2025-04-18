package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.controller.model.tournament.TournamentsDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveAllFantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament

class RetrieveFantaTournamentsService(private val retrieveAllFantaTournamentsRepository: RetrieveAllFantaTournamentsRepository) {

  fun retrieveAll(): FantaTournamentsResults =
      retrieveAllFantaTournamentsRepository.retrieve()
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
