package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.DomainTeam.FoundDomainTeam
import com.posadeus.fantatennis.domain.model.DomainTeam.NotFoundDomainTeam
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints

class RetrieveTeamService(private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository,
                          private val retrieveFantaTournamentsRepository: RetrieveFantaTournamentsRepository,
                          private val retrievePlayersPointsRepository: RetrievePlayersPointsRepository) {

  fun retrieve(teamId: Int): Team =
      when (val team = retrieveFantaTeamRepository.retrieveByTeamId(teamId)) {

        is NotFoundDomainTeam -> TeamIdNotFoundTeam
        is FoundDomainTeam ->
          when (val fantaTournament = retrieveFantaTournamentsRepository.retrieveBy(team.fantaTournamentId)) {

            is InvalidFantaTournament -> ErrorTeam
            is ValidFantaTournament ->
              when (val playersPoints = retrievePlayersPointsRepository.retrieveByYear(fantaTournament.tournamentYear)) {

                is InternalErrorPlayersPoints -> ErrorTeam
                is FoundPlayersPoints ->
                  playersPoints.playersPoints
                      .associateBy { it.playerId }
                      .let { pointsByPlayerId ->
                        team.players.keys.map { playerId -> toPlayerPoints(pointsByPlayerId[playerId], fantaTournament) }
                      }
                      .sortedByDescending { it.fantaPoints }
                      .let { players -> TeamDto(owner = team.ownerId, players = players, totalScore = players.sumOf { it.fantaPoints }) }
                      .let(::FoundTeam)
              }
          }
      }

  private fun toPlayerPoints(playerPoints: FoundPlayersPoints.PlayerPoints?, fantaTournament: ValidFantaTournament): PlayerPointsDto =
      PlayerPointsDto(fullName = playerPoints?.playerName ?: "",
                      fantaPoints = playerPoints
                          ?.pointsByTournament
                          ?.filterKeys { it in fantaTournament.startingTournamentId..fantaTournament.endingTournamentId }
                          ?.values?.sum() ?: 0.0)
}
