package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.model.DomainTeam
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournamentResults
import com.posadeus.fantatennis.domain.model.FantaTournamentResults.ErrorFantaTournamentResults
import com.posadeus.fantatennis.domain.model.FantaTournamentResults.NotFoundFantaTournamentId
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
        when (retrievePlayersPointsRepository.retrieveByYear(fantaTournament.tournamentYear)) {

          is InternalErrorPlayersPoints -> ErrorFantaTournamentResults
          is FoundPlayersPoints ->
            retrieveFantaTeamRepository.retrieveByFantaTournamentId(tournamentId)
                .teams
                .let { teams ->
                  if (teams.isEmpty()) return ErrorFantaTournamentResults
                  else if (teams.any { it is DomainTeam.NotFoundDomainTeam }) return ErrorFantaTournamentResults
                  else TODO()
                }
        }
    }
  }
}
