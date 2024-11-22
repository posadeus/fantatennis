package com.posadeus.fantatennis.domain.service.tournament

import com.posadeus.fantatennis.controller.model.tournament.TournamentCreatedDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.SuccessTournamentCreated
import com.posadeus.fantatennis.domain.model.TournamentCreated

class CreateTournamentService(private val fantaTournamentRepository: FantaTournamentsRepository) {

  fun create(dto: TournamentToCreateDto): TournamentCreated =
      (fantaTournamentRepository.create(dto) as ValidFantaTournament)
          .let(::toTournamentCreatedDto)
          .let(::SuccessTournamentCreated)

  private fun toTournamentCreatedDto(validFantaTournament: ValidFantaTournament): TournamentCreatedDto =
      TournamentCreatedDto(id = validFantaTournament.id,
                           startingTournamentId = validFantaTournament.startingTournamentId,
                           endingTournamentId = validFantaTournament.endingTournamentId,
                           tournamentYear = validFantaTournament.tournamentYear)
}
