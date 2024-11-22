package com.posadeus.fantatennis.domain.service.tournament

import com.posadeus.fantatennis.controller.model.tournament.TournamentCreatedDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament

class CreateTournamentService(private val fantaTournamentsRepository: FantaTournamentsRepository) {

  fun create(dto: TournamentToCreateDto): TournamentCreated =
      when (val result = fantaTournamentsRepository.create(dto)) {

        is ValidFantaTournament -> result.let(::toTournamentCreatedDto).let(::SuccessTournamentCreated)
        is InvalidFantaTournament -> ErrorTournamentCreation
      }

  private fun toTournamentCreatedDto(validFantaTournament: ValidFantaTournament): TournamentCreatedDto =
      TournamentCreatedDto(id = validFantaTournament.id,
                           startingTournamentId = validFantaTournament.startingTournamentId,
                           endingTournamentId = validFantaTournament.endingTournamentId,
                           tournamentYear = validFantaTournament.tournamentYear)
}
