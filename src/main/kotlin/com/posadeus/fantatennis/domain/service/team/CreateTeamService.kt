package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamCreatedDto
import com.posadeus.fantatennis.controller.model.team.TeamToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.CreateTeamRepository
import com.posadeus.fantatennis.domain.model.*

class CreateTeamService(private val createTeamRepository: CreateTeamRepository) {

  fun create(dto: TeamToCreateDto): TeamCreation =
      createTeamRepository.create(dto.ownerId, dto.tournamentId)
          .let(::toTeamCreation)

  private fun toTeamCreation(fantaTeam: FantaTeam): TeamCreation =
      when (fantaTeam) {

        is FantaTeamOk -> toTeamCreated(fantaTeam)
        is FantaTeamError -> ErrorTeamCreation
      }

  private fun toTeamCreated(fantaTeam: FantaTeamOk): TeamCreated =
      TeamCreatedDto(id = fantaTeam.id, ownerId = fantaTeam.ownerId)
          .let(::TeamCreated)
}
