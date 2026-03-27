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
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints.PlayerPoints
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
                    if (areTeamsInvalid(teams, playersPointsByPlayerId.keys)) {

                      ErrorFantaTournamentResults
                    }
                    else {

                      teams
                          .map { it as FoundDomainTeam }
                          .map { team ->
                            team.players
                                .flatMap { (playerId, tournamentRanges) ->
                                  playersPointsByPlayerId[playerId]!!
                                      .map { playerPoints ->
                                        calculateFantaPoints(playerPoints.pointsByTournament, tournamentRanges, fantaTournament)
                                            .let { toPlayerPoints(playerPoints, it) }
                                      }
                                }
                                .let { toTeamDto(team.ownerId, it.sortedByDescending { it.fantaPoints }) }
                          }
                          .sortedByDescending { it.totalScore }
                          .let(::FantaTournamentDto)
                          .let(::FoundFantaTournamentResults)
                    }
                  }
            }
          }
      }

  private fun areTeamsInvalid(teams: List<DomainTeam>, playerIds: Set<String>): Boolean =
      teams.isEmpty()
      || teams.any { it is NotFoundDomainTeam }
      || !teams.map { it as FoundDomainTeam }.all { team -> team.players.keys.all { it in playerIds } }

  private fun toPlayerPoints(playerPoints: PlayerPoints, fantaPoints: Double): PlayerPointsDto =
      PlayerPointsDto(fullName = playerPoints.playerName, fantaPoints = fantaPoints)

  private fun toTeamDto(ownerId: String, sortedPlayers: List<PlayerPointsDto>): TeamDto =
      TeamDto(owner = ownerId,
              players = sortedPlayers,
              totalScore = sortedPlayers.map { it.fantaPoints }.reduce { acc, points -> acc + points })

  private fun calculateFantaPoints(playerPoints: Map<TournamentId, Double>,
                                   tournamentRanges: Set<TournamentRange>,
                                   fantaTournament: ValidFantaTournament): Double =
      playerPoints
          .filterKeys { tournamentId ->
              tournamentRanges.any { range ->
                  isTournamentIdAfterOrEqualToStartingTournament(tournamentId, range.start, fantaTournament.startingTournamentId)
                  && isTournamentIdBeforeOrEqualToEndingTournament(tournamentId, range.end, fantaTournament.endingTournamentId)
              }
          }
          .map { it.value }
          .takeIf { it.isNotEmpty() }
          ?.reduce { acc, points -> acc + points }
      ?: 0.0

  private fun isTournamentIdAfterOrEqualToStartingTournament(tournamentId: Int,
                                                             rangeStart: Int,
                                                             fantaTournamentStart: Int): Boolean =
      tournamentId >= rangeStart && tournamentId >= fantaTournamentStart

  private fun isTournamentIdBeforeOrEqualToEndingTournament(tournamentId: Int,
                                                            rangeEnd: Int?,
                                                            fantaTournamentEnd: Int): Boolean =
      if (rangeEnd != null && rangeEnd <= fantaTournamentEnd) {

        rangeEnd >= tournamentId
      }
      else {

        fantaTournamentEnd >= tournamentId
      }
}
