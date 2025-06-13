package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.PlayersToSwapDto
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.infrastructure.TeamsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.Swap.SwapCompleted
import com.posadeus.fantatennis.domain.model.Swap.SwapFailed
import com.posadeus.fantatennis.domain.service.player.PlayerService
import com.posadeus.fantatennis.domain.service.team.RetrieveTeamService
import com.posadeus.fantatennis.domain.service.tournament.RetrieveTournamentsService

class SwapPlayersTeamService(private val retrieveTeamService: RetrieveTeamService,
                             private val playerService: PlayerService,
                             private val retrieveTournamentsService: RetrieveTournamentsService,
                             private val teamsRepository: TeamsRepository,
                             private val swapPlayersRepository: SwapPlayersRepository) {

  @Deprecated("Use the new version")
  fun swap(teamId: Int, playersToSwap: PlayersToSwapDto): Team =
      when (val team = retrieveTeamService.retrieve(teamId)) {

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
                when (toSwapCommand(teamId, playersToSwap).let(teamsRepository::swapPlayers)) {
                  is SwapCompleted -> retrieveTeamService.retrieve(teamId)
                  is SwapFailed -> ErrorTeam
                }
              }
          ?: ErrorTeam
        }

        is TeamIdNotFoundTeam, ErrorTeam -> team
      }

  private fun toSwapCommand(teamId: Int, playersToSwapDto: PlayersToSwapDto) =
      SwapCommand(teamId = teamId,
                  playersToRemove = playersToSwapDto.remove.playerIds,
                  playersToAdd = playersToSwapDto.add.playerIds,
                  endingTournament = playersToSwapDto.remove.endingTournamentId,
                  startingTournament = playersToSwapDto.add.startingTournamentId)

  private fun areAllRequestedPlayersPresent(allPlayersIds: List<String>, playersToSwap: PlayersToSwapDto) =
      allPlayersIds.isNotEmpty()
      && allPlayersIds.containsAll(playersToSwap.add.playerIds)
      && allPlayersIds.containsAll(playersToSwap.remove.playerIds)

  fun swapNew(teamId: Int, playersToSwap: PlayersToSwapDto): Team {
    return swapPlayersRepository.swap(teamId, playersToSwap)
  }
}
