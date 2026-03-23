package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentDto
import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.DomainTeam.FoundDomainTeam
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournamentResults.*
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints

class RetrieveFantaTournamentService(private val retrieveFantaTournamentsRepository: RetrieveFantaTournamentsRepository,
                                     private val retrievePlayersPointsRepository: RetrievePlayersPointsRepository,
                                     private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository) {

  fun retrieve(tournamentId: Int): FantaTournamentResults =
      when (val fantaTournament = retrieveFantaTournamentsRepository.retrieveBy(tournamentId)) {

        is InvalidFantaTournament -> NotFoundFantaTournamentId
        is ValidFantaTournament ->
          when (val playersPoints = retrievePlayersPointsRepository.retrieveByYear(fantaTournament.tournamentYear)) {

            is InternalErrorPlayersPoints -> ErrorFantaTournamentResults
            is FoundPlayersPoints -> {

              val playersPointsByPlayerId = playersPoints.playersPoints.groupBy { it.playerId }

              retrieveFantaTeamRepository.retrieveByFantaTournamentId(tournamentId)
                  .teams
                  .let { teams ->
                    if (teams.isEmpty() || teams.any { it is NotFoundDomainTeam }) 
                      ErrorFantaTournamentResults
                    else {

                      val domainTeams = teams.map { it as FoundDomainTeam }

                      if (areTeamsPlayersPresentInTheList(domainTeams, playersPointsByPlayerId.keys)) {

                        domainTeams
                            .map { team ->
                              team.players
                                  .flatMap { entry ->
                                    playersPointsByPlayerId[entry.key]!!
                                        .map { toPlayerPointsDto(it, entry, fantaTournament) }
                                  }
                                  .let { toTeamDto(team.ownerId, it.sortedByDescending { it.fantaPoints }) }
                            }
                            .sortedByDescending { it.totalScore }
                            .let(::FantaTournamentDto)
                            .let(::FoundFantaTournamentResults)
                      }
                      else 
                        ErrorFantaTournamentResults
                    }
                  }
            }
          }
      }

  private fun toTeamDto(ownerId: String,
                        sortedPlayers: List<PlayerPointsDto>): TeamDto =
      TeamDto(owner = ownerId,
              players = sortedPlayers,
              totalScore = sortedPlayers.map { it.fantaPoints }.reduce { acc, points -> acc + points })

  private fun toPlayerPointsDto(playerPointsByPlayerId: FoundPlayersPoints.PlayerPoints,
                                tournamentRangeByPlayerId: Map.Entry<PlayerId, TournamentRange>,
                                fantaTournament: ValidFantaTournament): PlayerPointsDto =
      playerPointsByPlayerId.pointsByTournament
          .filterKeys { isTournamentIdAfterOrEqualToStartingTournament(it, tournamentRangeByPlayerId.value.start, fantaTournament.startingTournamentId) }
          .filterKeys { isTournamentIdBeforeOrEqualToEndingTournament(it, tournamentRangeByPlayerId.value.end, fantaTournament.endingTournamentId) }
          .map { it.value }
          .takeIf { it.isNotEmpty() }
          ?.reduce { acc, points -> acc + points }
          .let { PlayerPointsDto(fullName = playerPointsByPlayerId.playerName, fantaPoints = it ?: 0.0) }

  private fun isTournamentIdAfterOrEqualToStartingTournament(tournamentId: Int,
                                                             tournamentIdRangeStart: Int,
                                                             fantaTournamentIdStart: Int): Boolean =
      tournamentId >= tournamentIdRangeStart && tournamentId >= fantaTournamentIdStart

  private fun isTournamentIdBeforeOrEqualToEndingTournament(tournamentId: Int,
                                                            tournamentIdRangeEnd: Int?,
                                                            fantaTournamentIdEnd: Int): Boolean =
      if (tournamentIdRangeEnd != null && tournamentIdRangeEnd <= fantaTournamentIdEnd) {

        tournamentIdRangeEnd >= tournamentId
      }
      else {

        fantaTournamentIdEnd >= tournamentId
      }

  private fun areTeamsPlayersPresentInTheList(teams: List<FoundDomainTeam>, playerIds: Set<String>): Boolean =
      teams.all { team -> team.players.keys.all { it in playerIds } }
}
