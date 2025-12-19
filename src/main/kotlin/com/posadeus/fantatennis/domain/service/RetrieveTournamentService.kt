package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto
import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.infrastructure.RetrieveTournamentsRepository
import com.posadeus.fantatennis.domain.model.TournamentResults
import com.posadeus.fantatennis.domain.model.TournamentResults.FoundTournamentResults

class RetrieveTournamentService(private val retrieveTournamentsRepository: RetrieveTournamentsRepository,
                                private val retrievePlayersPointsRepository: RetrievePlayersPointsRepository) {

  fun retrieve(tournamentId: Int): TournamentResults {

    val tournament = retrieveTournamentsRepository.retrieveBy(tournamentId)
    val playerPoints = retrievePlayersPointsRepository.retrieveBy(tournamentId)

    return FoundTournamentResults(tournament = TournamentDto(tournamentName = tournament.name,
                                                             tournamentPoints = tournament.points,
                                                             playersScore = playerPoints.map { PlayerPointsDto(fullName = it.playerName,
                                                                                                               fantaPoints = it.totalPoints) }
                                                                 .sortedByDescending { it.fantaPoints }))
  }
}
