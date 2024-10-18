package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamCreatedDto
import com.posadeus.fantatennis.controller.model.team.TeamToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.model.*

class CreateTeamService(private val repository: FantaTeamsRepository) {

  fun create(request: TeamToCreateDto): TeamCreation =
      repository.createTeam(request.ownerId)
          .let(::convert)

  private fun convert(fantaTeam: FantaTeam) =
      TeamCreatedDto(id = fantaTeam.id, ownerId = fantaTeam.ownerId)
          .let(::TeamCreated)
}
