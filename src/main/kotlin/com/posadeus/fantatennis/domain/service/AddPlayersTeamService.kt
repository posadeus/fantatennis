package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.TeamsRepository
import com.posadeus.fantatennis.domain.model.*
import org.slf4j.LoggerFactory

class AddPlayersTeamService(private val teamsRepository: TeamsRepository) {

  fun addPlayers(teamId: Int, playerIds: Set<String>): Team =
      when (val addPlayers = teamsRepository.addPlayers(teamId, playerIds)) {

        is AddPlayersOk -> toFoundTeam(addPlayers.players)
        is AddPlayersTeamNotFound -> TeamIdNotFoundTeam
        is PlayersNotFound -> ErrorTeam.also { LOGGER.error("Players not found: ${addPlayers.missingPlayersIds}") }
        is AddPlayersError -> ErrorTeam
      }

  private fun toFoundTeam(players: Set<DomainPlayer>): FoundTeam =
      players
          .map { TeamPlayerDto(fullName = it.fullName, fantaPoints = 0.0) }
          .let { FoundTeam(team = TeamDto(players = it, totalScore = 0.0)) }

  companion object {

    private val LOGGER = LoggerFactory.getLogger(AddPlayersTeamService::class.java)
  }
}
