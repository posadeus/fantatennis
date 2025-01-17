package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsTeamsRepository
import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.model.*

class RetrieveTeamService(private val fantaTournamentsTeamsRepository: FantaTournamentsTeamsRepository,
                          private val playerPointsRepository: PlayerPointsRepository) {

  fun getTeam(teamId: Int): Team =
      when (val tournamentByTeamId = fantaTournamentsTeamsRepository.retrieveTournamentByTeamId(teamId)) {

        is FoundTournamentByTeam -> retrieveTeam(tournamentByTeamId)
        is EmptyTournamentByTeam -> TeamIdNotFoundTeam
      }

  private fun retrieveTeam(tournamentByTeamId: FoundTournamentByTeam): Team =
      tournamentByTeamId
          .let(playerPointsRepository::retrieve)
          .playerPoints
          .map(::toTeamPlayerDto)
          .let(::toTeamDto)
          .let(::FoundTeam)

  private fun toTeamPlayerDto(it: PlayerPoints) =
      TeamPlayerDto(fullName = it.playerName,
                    fantaPoints = it.totalPoints)

  private fun toTeamDto(teamPlayers: List<TeamPlayerDto>) =
      TeamDto(owner = "", // FIXME (minor/not urgent) add owner
              players = teamPlayers,
              totalScore = teamPlayers.sumOf { it.fantaPoints })
}
