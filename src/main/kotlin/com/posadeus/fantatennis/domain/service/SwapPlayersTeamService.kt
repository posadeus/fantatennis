package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.PlayersToSwapDto
import com.posadeus.fantatennis.domain.infrastructure.TeamsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.service.player.PlayerService
import com.posadeus.fantatennis.domain.service.team.RetrieveTeamService
import com.posadeus.fantatennis.domain.service.tournament.RetrieveTournamentsService

// TODO Add configuration
class SwapPlayersTeamService(private val retrieveTeamService: RetrieveTeamService,
                             private val playerService: PlayerService,
                             private val retrieveTournamentsService: RetrieveTournamentsService,
                             private val teamsRepository: TeamsRepository) {

  fun swap(teamId: Int, playersToSwap: PlayersToSwapDto): Team {

    when (val team = retrieveTeamService.getTeam(teamId)) {

      is FoundTeam -> {

        val allPlayersIds = playerService.allPlayers()
            .map(DomainPlayer::id)

        if (areAllRequestedPlayersPresent(allPlayersIds, playersToSwap)) {

          val tournaments = retrieveTournamentsService.retrieveAll()
          val tournamentsIds = tournaments
              .map(Tournament::id)

          return if (tournaments.isNotEmpty()
                     && playersToSwap.add.startingTournamentId in tournamentsIds
                     && playersToSwap.remove.endingTournamentId in tournamentsIds) {

            teamsRepository.swapPlayers(SwapCommand(playersToSwap.remove.playerIds,
                                                    playersToSwap.add.playerIds,
                                                    playersToSwap.remove.endingTournamentId,
                                                    playersToSwap.add.startingTournamentId))
          }
          else
            ErrorTeam
        }
        else 
          return ErrorTeam
      }

      is TeamIdNotFoundTeam, ErrorTeam -> return team
    }
  }

  private fun areAllRequestedPlayersPresent(allPlayersIds: List<String>, playersToSwap: PlayersToSwapDto) = 
      allPlayersIds.isNotEmpty()
       && allPlayersIds.containsAll(playersToSwap.add.playerIds)
       && allPlayersIds.containsAll(playersToSwap.remove.playerIds)
}
