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

  fun create(request: TeamToCreateDto): TeamCreation {

    return if (request.tournament.id != null) {

      when (fantaTournamentsRepository.retrieve(request.tournament.id)) {

        is ValidFantaTournament -> teamCreation(request)
        is InvalidFantaTournament -> {

          if (request.tournament.startingTournamentId != null
              && request.tournament.endingTournamentId != null
              && request.tournament.tournamentYear != null) {
            teamAndTournamentCreation(request)
          }
          else {

            ErrorTeamCreation
          }
        }
      }
    }
    else {

      teamAndTournamentCreation(request)
    }
  }

  private fun teamCreation(request: TeamToCreateDto): TeamCreation =
      when (val fantaTeam = fantaTeamsRepository.createTeam(request.ownerId, request.tournament.id)) {

        is FantaTeamOk -> toTeamCreated(fantaTeam)
        is FantaTeamError -> ErrorTeamCreation
      }

  private fun teamAndTournamentCreation(request: TeamToCreateDto): TeamCreation =
      when (val fantaTeam = fantaTeamsRepository.createTeamAndTournament(request.ownerId, request.tournament)) {

        is FantaTeamOk -> toTeamCreated(fantaTeam)
        is FantaTeamError -> ErrorTeamCreation
      }

  private fun toTeamCreated(fantaTeam: FantaTeamOk): TeamCreated =
      TeamCreatedDto(id = fantaTeam.id, ownerId = fantaTeam.ownerId)
          .let(::TeamCreated)
}
