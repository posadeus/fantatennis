package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamCreatedDto
import com.posadeus.fantatennis.controller.model.team.TeamToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament

class CreateTeamService(private val fantaTeamsRepository: FantaTeamsRepository,
                        private val fantaTournamentsRepository: FantaTournamentsRepository) {

  fun create(dto: TeamToCreateDto): TeamCreation =
      when (val tournament = fantaTournamentsRepository.retrieve(dto.tournamentId)) {

        is ValidFantaTournament -> createTeam(dto.ownerId, tournament)
        is InvalidFantaTournament -> ErrorTeamCreation
      }

  private fun createTeam(ownerId: String, tournament: ValidFantaTournament): TeamCreation =
      fantaTeamsRepository.createTeam(ownerId, tournament)
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
