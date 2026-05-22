package com.posadeus.fantatennis.domain.service.team

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
              when (retrievePlayersPointsRepository.retrieveByYear(fantaTournament.tournamentYear)) {

                is InternalErrorPlayersPoints -> ErrorTeam
                is FoundPlayersPoints -> ErrorTeam
              }
          }
      }
}
