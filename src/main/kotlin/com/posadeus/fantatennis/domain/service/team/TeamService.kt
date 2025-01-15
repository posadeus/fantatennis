package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsTeamsRepository
import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.model.*

class TeamService(private val fantaTournamentsTeamsRepository: FantaTournamentsTeamsRepository,
                  private val playerPointsRepository: PlayerPointsRepository) {

  fun getTeam(teamId: Int): Team =
      when (val tournamentByTeamId = fantaTournamentsTeamsRepository.retrieveTournamentByTeamId(teamId)) {

        is FoundTournamentByTeam -> retrieveTeam(tournamentByTeamId)
        is EmptyTournamentByTeam -> TeamIdNotFoundTeam
      }

  private fun retrieveTeam(tournamentByTeamId: FoundTournamentByTeam): Team =
      playerPointsRepository.retrieve(tournamentByTeamId).playerPoints
          .map { TeamPlayerDto(fullName = it.playerName, fantaPoints = it.totalPoints) }
          .let(::toTeamDto)
          .let(::FoundTeam)

  private fun toTeamDto(teamPlayers: List<TeamPlayerDto>) =
      TeamDto(owner = "", // FIXME
              players = teamPlayers,
              totalScore = teamPlayers.sumOf { it.fantaPoints })
}
