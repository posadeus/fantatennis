package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.model.FoundTeam
import com.posadeus.fantatennis.domain.model.Team
import com.posadeus.fantatennis.infrastructure.TeamsRepository

class AddPlayersTeamService(private val teamsRepository: TeamsRepository) {

  fun addPlayers(teamId: Int, playerIds: Set<String>): Team =
      teamsRepository.addPlayers(teamId, playerIds)
          .map { TeamPlayerDto(fullName = it.fullName, fantaPoints = 0.0) }
          .let { FoundTeam(team = TeamDto(players = it, totalScore = 0.0)) }
}
