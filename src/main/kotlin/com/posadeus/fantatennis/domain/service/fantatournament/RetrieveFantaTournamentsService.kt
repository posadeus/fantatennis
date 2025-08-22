package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.controller.model.tournament.TournamentsDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveAllFantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournaments.Invalid
import com.posadeus.fantatennis.domain.model.FantaTournaments.Valid

class RetrieveFantaTournamentsService(private val retrieveAllFantaTournamentsRepository: RetrieveAllFantaTournamentsRepository) {

  fun retrieveAll(): FantaTournamentsResults =
      when (val result = retrieveAllFantaTournamentsRepository.retrieve()) {

        is Invalid -> ErrorFantaTournamentsResults
        is Valid -> {
          result
              .takeIf { it.tournaments.isNotEmpty() }
              ?.tournaments
              ?.map(ValidFantaTournament::id)
              ?.let(::TournamentsDto)
              ?.let(::FoundFantaTournamentsResults)
          ?: NotFoundFantaTournaments
        }
      }
}