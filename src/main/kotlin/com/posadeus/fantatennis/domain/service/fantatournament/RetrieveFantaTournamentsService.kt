package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentsDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournaments.Invalid
import com.posadeus.fantatennis.domain.model.FantaTournaments.Valid

class RetrieveFantaTournamentsService(private val retrieveFantaTournamentsRepository: RetrieveFantaTournamentsRepository) {

  fun retrieveAll(): FantaTournamentsResults =
      when (val result = retrieveFantaTournamentsRepository.retrieve()) {

        is Invalid -> ErrorFantaTournamentsResults
        is Valid -> {
          result
              .takeIf { it.tournaments.isNotEmpty() }
              ?.tournaments
              ?.map(ValidFantaTournament::id)
              ?.let(::FantaTournamentsDto)
              ?.let(::FoundFantaTournamentsResults)
          ?: NotFoundFantaTournaments
        }
      }
}