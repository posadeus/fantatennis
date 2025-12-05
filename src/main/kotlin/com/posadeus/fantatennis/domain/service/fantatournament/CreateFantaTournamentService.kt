package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentCreatedDto
import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.CreateFantaTournamentRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament

class CreateFantaTournamentService(private val createFantaTournamentsRepository: CreateFantaTournamentRepository) {

  fun create(dto: FantaTournamentToCreateDto): TournamentCreated =
      when (val result = createFantaTournamentsRepository.create(dto)) {

        is ValidFantaTournament -> result.let(::toTournamentCreatedDto).let(::SuccessTournamentCreated)
        is InvalidFantaTournament -> ErrorTournamentCreation
      }

  private fun toTournamentCreatedDto(validFantaTournament: ValidFantaTournament): FantaTournamentCreatedDto =
      FantaTournamentCreatedDto(id = validFantaTournament.id,
                           startingTournamentId = validFantaTournament.startingTournamentId,
                           endingTournamentId = validFantaTournament.endingTournamentId,
                           tournamentYear = validFantaTournament.tournamentYear)
}
