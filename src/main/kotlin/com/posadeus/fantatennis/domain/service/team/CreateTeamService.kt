package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamCreatedDto
import com.posadeus.fantatennis.controller.model.team.TeamToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.model.*

class CreateTeamService(private val repository: FantaTeamsRepository) {

  fun create(request: TeamToCreateDto): TeamCreation =
      when (val fantaTeam = repository.createTeam(request.ownerId)) {

        is FantaTeamOk -> toTeamCreated(fantaTeam)
        is FantaTeamError -> ErrorTeamCreation
      }

  private fun toTeamCreated(fantaTeam: FantaTeamOk): TeamCreated =
      TeamCreatedDto(id = fantaTeam.id, ownerId = fantaTeam.ownerId)
          .let(::TeamCreated)
}
