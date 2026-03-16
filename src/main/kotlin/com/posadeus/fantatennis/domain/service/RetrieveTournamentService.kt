package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto
import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.domain.infrastructure.RetrieveTournamentsRepository
import com.posadeus.fantatennis.domain.model.PlayersPoints.FoundPlayersPoints
import com.posadeus.fantatennis.domain.model.PlayersPoints.InternalErrorPlayersPoints
import com.posadeus.fantatennis.domain.model.Tournament.*
import com.posadeus.fantatennis.domain.model.TournamentResults
import com.posadeus.fantatennis.domain.model.TournamentResults.*

class RetrieveTournamentService(private val retrieveTournamentsRepository: RetrieveTournamentsRepository,
                                private val retrievePlayersPointsRepository: RetrievePlayersPointsRepository) {

  fun retrieve(tournamentId: Int): TournamentResults =
      when (val tournament = retrieveTournamentsRepository.retrieveBy(tournamentId)) {

        is FoundTournament ->
          tournamentId
              .let(::getPlayersScore)
              .takeIf(List<PlayerPointsDto>::isNotEmpty)
              ?.let { toTournamentDto(tournament, it) }
              ?.let(::FoundTournamentResults)
          ?: ErrorTournamentResults

        is NotFoundTournament -> NotFoundTournamentId
        is InternalErrorTournament -> ErrorTournamentResults
      }

  private fun getPlayersScore(tournamentId: Int) =
      when (val playersPointsResult = retrievePlayersPointsRepository.retrieveByTournamentId(tournamentId)) {

        is FoundPlayersPoints -> playersPointsResult
            .playersPoints
            .map { PlayerPointsDto(fullName = it.playerName, fantaPoints = it.totalPoints) }
            .sortedByDescending { it.fantaPoints }

        is InternalErrorPlayersPoints -> emptyList()
      }

  private fun toTournamentDto(tournament: FoundTournament,
                              playersPointsDto: List<PlayerPointsDto>) =
      TournamentDto(tournamentName = tournament.name,
                    tournamentPoints = tournament.points,
                    playersScore = playersPointsDto)
}
