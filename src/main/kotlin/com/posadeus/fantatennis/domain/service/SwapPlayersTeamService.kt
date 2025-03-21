package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.PlayersToSwapDto
import com.posadeus.fantatennis.domain.infrastructure.TeamsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.service.player.PlayerService
import com.posadeus.fantatennis.domain.service.team.RetrieveTeamService
import com.posadeus.fantatennis.domain.service.tournament.RetrieveTournamentsService

class SwapPlayersTeamService(private val retrieveTeamService: RetrieveTeamService,
                             private val playerService: PlayerService,
                             private val retrieveTournamentsService: RetrieveTournamentsService,
                             private val teamsRepository: TeamsRepository) {

  fun swap(teamId: Int, playersToSwap: PlayersToSwapDto): Team =
      when (val team = retrieveTeamService.getTeam(teamId)) {

        is FoundTeam -> {

          playerService.allPlayers()
              .map(DomainPlayer::id)
              .takeIf { areAllRequestedPlayersPresent(it, playersToSwap) }
              ?.flatMap {
                retrieveTournamentsService.retrieveAll()
                  .map(Tournament::id)
              }
              // TODO: Add a rule to fail the service if the startingTournament is before the endingTournament OR already started OR not right after the endingTournament
              ?.takeIf {
                it.isNotEmpty()
                && playersToSwap.add.startingTournamentId in it
                && playersToSwap.remove.endingTournamentId in it
              }
              ?.let {
                playersToSwap
                    .let(::toSwapCommand)
                    .let(teamsRepository::swapPlayers)
              }
          ?: ErrorTeam
        }

        is TeamIdNotFoundTeam, ErrorTeam -> team
      }

  private fun toSwapCommand(playersToSwap: PlayersToSwapDto) =
      SwapCommand(playersToSwap.remove.playerIds,
                  playersToSwap.add.playerIds,
                  playersToSwap.remove.endingTournamentId,
                  playersToSwap.add.startingTournamentId)

  private fun areAllRequestedPlayersPresent(allPlayersIds: List<String>, playersToSwap: PlayersToSwapDto) =
      allPlayersIds.isNotEmpty()
      && allPlayersIds.containsAll(playersToSwap.add.playerIds)
      && allPlayersIds.containsAll(playersToSwap.remove.playerIds)
}
