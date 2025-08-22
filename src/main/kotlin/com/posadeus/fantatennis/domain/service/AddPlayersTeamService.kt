package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.exception.InvalidAddPlayersException
import com.posadeus.fantatennis.domain.infrastructure.AddPlayersToTeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.*
import com.posadeus.fantatennis.domain.model.AddPlayers.ValidAddPlayers
import org.slf4j.LoggerFactory

class AddPlayersTeamService(private val addPlayerToTeamRepository: AddPlayersToTeamRepository) {

  fun addPlayers(teamId: Int, playerIds: Set<String>, startingTournamentId: Int): Team =
      try {

        when (val addPlayers = addPlayerToTeamRepository.add(teamId, playerIds, startingTournamentId)) {

          is ValidAddPlayers -> toFoundTeam(addPlayers.players)
          is AddPlayersTeamNotFound -> TeamIdNotFoundTeam
          is AddPlayersTournamentNotFound -> ErrorTeam.also { LOGGER.error("Tournament not found: $startingTournamentId") } // FIXME: not a generic error
          is PlayersNotFound -> ErrorTeam.also { LOGGER.error("Players not found: ${addPlayers.missingPlayerIds}") }
        }
      }
      catch (e: InvalidAddPlayersException) {

        ErrorTeam.also { LOGGER.error(e.message) }
      }

  private fun toFoundTeam(players: Set<DomainPlayer>): FoundTeam =
      players
          .map { TeamPlayerDto(fullName = it.fullName, fantaPoints = 0.0) }
          .let { FoundTeam(team = TeamDto(players = it, totalScore = 0.0)) }

  companion object {

    private val LOGGER = LoggerFactory.getLogger(AddPlayersTeamService::class.java)
  }
}
