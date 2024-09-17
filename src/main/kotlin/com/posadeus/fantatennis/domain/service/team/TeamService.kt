package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsTeamsRepository
import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.model.FoundTeam
import com.posadeus.fantatennis.domain.model.Team

class TeamService(private val fantaTournamentsTeamsRepository: FantaTournamentsTeamsRepository,
                  private val playerPointsRepository: PlayerPointsRepository) {

  fun getTeam(teamId: Int): Team {

    val retrieveTournamentByTeamId = fantaTournamentsTeamsRepository.retrieveTournamentByTeamId(teamId)

    val retrieve = playerPointsRepository.retrieve(retrieveTournamentByTeamId)

    return retrieve.playerPoints.map { TeamPlayerDto(fullName = "", fantaPoints = it.totalPoints) }
        .let { TeamDto(it, it.sumOf { it.fantaPoints }) }
        .let { FoundTeam(it) }
  }
}
