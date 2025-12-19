package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto
import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.infrastructure.RetrieveTournamentsRepository
import com.posadeus.fantatennis.domain.model.Tournament.FoundTournament
import com.posadeus.fantatennis.domain.model.Tournament.NotFoundTournament
import com.posadeus.fantatennis.domain.model.TournamentResults
import com.posadeus.fantatennis.domain.model.TournamentResults.FoundTournamentResults
import com.posadeus.fantatennis.domain.model.TournamentResults.NotFoundTournamentId

class RetrieveTournamentService(private val retrieveTournamentsRepository: RetrieveTournamentsRepository,
                                private val retrievePlayersPointsRepository: RetrievePlayersPointsRepository) {

  fun retrieve(tournamentId: Int): TournamentResults =
      when (val tournament = retrieveTournamentsRepository.retrieveBy(tournamentId)) {

        is FoundTournament -> FoundTournamentResults(tournament = TournamentDto(tournamentName = tournament.name,
                                                                                tournamentPoints = tournament.points,
                                                                                playersScore = getPlayersScore(tournamentId)))

        is NotFoundTournament -> NotFoundTournamentId
      }

  private fun getPlayersScore(tournamentId: Int) =
      retrievePlayersPointsRepository.retrieveBy(tournamentId)
          .map { PlayerPointsDto(fullName = it.playerName, fantaPoints = it.totalPoints) }
          .sortedByDescending { it.fantaPoints }
}
