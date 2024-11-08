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
        when (val tournament = fantaTournamentsRepository.retrieve(dto.tournament.id)) {

          is ValidFantaTournament -> createTeam(dto.ownerId, tournament)
          is InvalidFantaTournament -> createTeamAndTournament(dto)
        }
      else createTeamAndTournament(dto)

  private fun createTeam(ownerId: String, tournament: ValidFantaTournament): TeamCreation =
      fantaTeamsRepository.createTeam(ownerId, toTournamentCreationDto(tournament))
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

  private fun toTournamentCreationDto(tournament: ValidFantaTournament): TournamentCreationDto =
      TournamentCreationDto(id = tournament.id,
                            startingTournamentId = tournament.startingTournamentId,
                            endingTournamentId = tournament.endingTournamentId,
                            tournamentYear = tournament.tournamentYear)

  private fun toTeamCreated(fantaTeam: FantaTeamOk): TeamCreated =
      TeamCreatedDto(id = fantaTeam.id, ownerId = fantaTeam.ownerId)
          .let(::TeamCreated)

  private fun isValidTournamentDto(tournament: TournamentCreationDto) =
      (tournament.startingTournamentId != null
       && tournament.endingTournamentId != null
       && tournament.tournamentYear != null)
}
