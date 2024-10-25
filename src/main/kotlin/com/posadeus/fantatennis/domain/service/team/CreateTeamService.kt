package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.*
import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament

class CreateTeamService(private val fantaTeamsRepository: FantaTeamsRepository,
                        private val fantaTournamentsRepository: FantaTournamentsRepository) {

  fun create(dto: TeamToCreateDto): TeamCreation =
      if (dto.tournament.id != null)
        when (fantaTournamentsRepository.retrieve(dto.tournament.id)) {

          is ValidFantaTournament -> createTeam(dto)
          is InvalidFantaTournament -> createTeamAndTournament(dto)
        }
      else createTeamAndTournament(dto)

  private fun createTeam(request: TeamToCreateDto): TeamCreation =
      fantaTeamsRepository.createTeam(request.ownerId, request.tournament.id)
          .let(::toTeamCreation)

  private fun createTeamAndTournament(request: TeamToCreateDto): TeamCreation =
      if (isValidTournamentDto(request.tournament))
        fantaTeamsRepository.createTeamAndTournament(request.ownerId, request.tournament)
            .let(::toTeamCreation)
      else ErrorTeamCreation

  private fun toTeamCreation(fantaTeam: FantaTeam): TeamCreation =
      when (fantaTeam) {

        is FantaTeamOk -> toTeamCreated(fantaTeam)
        is FantaTeamError -> ErrorTeamCreation
      }

  private fun toTeamCreated(fantaTeam: FantaTeamOk): TeamCreated =
      TeamCreatedDto(id = fantaTeam.id, ownerId = fantaTeam.ownerId)
          .let(::TeamCreated)

  private fun isValidTournamentDto(tournament: TournamentCreationDto) =
      (tournament.startingTournamentId != null
       && tournament.endingTournamentId != null
       && tournament.tournamentYear != null)
}
