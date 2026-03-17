package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentDto
import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.model.DomainTeam.FoundDomainTeam
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournamentResults
import com.posadeus.fantatennis.domain.model.FantaTournamentResults.*
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints

class RetrieveFantaTournamentService(private val retrieveFantaTournamentResultsRepository: RetrieveFantaTournamentResultsRepository,
                                     private val retrieveFantaTournamentsRepository: RetrieveFantaTournamentsRepository,
                                     private val retrievePlayersPointsRepository: RetrievePlayersPointsRepository,
                                     private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository) {

  fun retrieve(tournamentId: Int): FantaTournamentResults {

    return when (val fantaTournament = retrieveFantaTournamentsRepository.retrieveBy(tournamentId)) {

      is InvalidFantaTournament -> NotFoundFantaTournamentId
      is ValidFantaTournament ->
        when (val playersPoints = retrievePlayersPointsRepository.retrieveByYear(fantaTournament.tournamentYear)) {

          is InternalErrorPlayersPoints -> ErrorFantaTournamentResults
          is FoundPlayersPoints -> {

            val playersPointsByPlayerId = playersPoints.playersPoints.groupBy { it.playerId }

            retrieveFantaTeamRepository.retrieveByFantaTournamentId(tournamentId)
                .teams
                .let { teams ->
                  if (teams.isEmpty()) return ErrorFantaTournamentResults
                  else if (teams.any { it is NotFoundDomainTeam }) return ErrorFantaTournamentResults
                  else {

                    if (teams.all { (it as FoundDomainTeam).players.all { it.key in playersPointsByPlayerId.keys } }) {

                      teams
                          .map { it as FoundDomainTeam }
                          .map { team ->
                            team.players
                                .flatMap { entry ->
                                  playersPointsByPlayerId[entry.key]!!
                                      .map { playerPointsByPlayerId ->
                                        playerPointsByPlayerId.pointsByTournament
                                            .filterKeys { entry.value.start <= it }
                                            .filterKeys {
                                              if (entry.value.end != null && entry.value.end!! <= fantaTournament.endingTournamentId) {

                                                entry.value.end!! >= it
                                              }
                                              else {

                                                fantaTournament.endingTournamentId >= it
                                              }
                                            }
                                            .map { it.value }
                                            .takeIf { it.isNotEmpty() }
                                            ?.reduce { acc, points -> acc + points }
                                            .let { PlayerPointsDto(fullName = playerPointsByPlayerId.playerName, fantaPoints = it ?: 0.0) }
                                      }
                                }
                                .let { TeamDto(owner = team.ownerId, players = it.sortedByDescending { it.fantaPoints }, totalScore = it.map { it.fantaPoints }.reduce { acc, points -> acc + points }) }
                          }
                          .let { FoundFantaTournamentResults(FantaTournamentDto(it)) }
                    }
                    else ErrorFantaTournamentResults
                  }
                }
          }
        }
    }
  }
}
