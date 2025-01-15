package com.posadeus.fantatennis.domain.service.tournament

import com.posadeus.fantatennis.controller.model.tournament.TournamentsDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament

class RetrieveTournamentsService(private val fantaTournamentsRepository: FantaTournamentsRepository) {

  fun retrieveAll(): FantaTournamentsResults {

    val results = fantaTournamentsRepository.retrieveAll()

    if (results.isEmpty())
      return NotFoundFantaTournaments


    if (results.any { it is InvalidFantaTournament })
      return ErrorFantaTournamentsResults

    return toFantaTournamentsResults(results as List<ValidFantaTournament>)
  }

  private fun toFantaTournamentsResults(results: List<ValidFantaTournament>): FoundFantaTournamentsResults =
      results
          .map { it.id }
          .let(::TournamentsDto)
          .let(::FoundFantaTournamentsResults)
}
