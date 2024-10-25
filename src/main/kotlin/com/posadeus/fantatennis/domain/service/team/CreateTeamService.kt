package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamCreatedDto
import com.posadeus.fantatennis.controller.model.team.TeamToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*

class CreateTeamService(private val fantaTeamsRepository: FantaTeamsRepository,
                        private val fantaTournamentsRepository: FantaTournamentsRepository) {

  fun create(request: TeamToCreateDto): TeamCreation {

    val fantaTournament = fantaTournamentsRepository.retrieve(request.tournament.id)

    return when (val fantaTeam = fantaTeamsRepository.createTeam(request.ownerId, request.tournament.id)) {

      is FantaTeamOk -> toTeamCreated(fantaTeam)
      is FantaTeamError -> ErrorTeamCreation
    }
  }

  private fun toTeamCreated(fantaTeam: FantaTeamOk): TeamCreated =
      TeamCreatedDto(id = fantaTeam.id, ownerId = fantaTeam.ownerId)
          .let(::TeamCreated)
}
