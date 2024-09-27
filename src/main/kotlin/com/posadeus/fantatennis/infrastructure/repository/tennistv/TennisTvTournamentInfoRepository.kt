package com.posadeus.fantatennis.infrastructure.repository.tennistv

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.Round
import com.posadeus.fantatennis.domain.model.Round.*
import com.posadeus.fantatennis.infrastructure.client.tennistv.TennisTvClient
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.*
import com.posadeus.fantatennis.infrastructure.repository.exception.UnexpectedRoundException

class TennisTvTournamentInfoRepository(private val client: TennisTvClient) : TournamentInfoRepository {

  override fun canProcess(tournamentId: Int): Boolean =
      true

  override fun retrieveTournamentInfo(tournamentId: Int, year: Int): TournamentInfo =
      when (val response = client.retrieveTournamentInfo(tournamentId, year)) {

        is TennisTvTournamentOkResponse -> toTournamentInfo(tournamentId, response)
        is TennisTvTournamentErrorResponse -> ErrorTournamentInfo
      }

  private fun toTournamentInfo(tournamentId: Int, response: TennisTvTournamentOkResponse): CompleteTournamentInfo {

    val tournament = response.tournament.MS

    val winners = tournament.Rounds
        .associate { round ->
          (toRound(round.RoundName)
              to winners(round.Fixtures))
        }

    val participants =
        tournament.Rounds.last().Fixtures
            .flatMap { Pair(it.Result?.TeamTop?.Player?.PlayerId, it.Result?.TeamBottom?.Player?.PlayerId).toList() }
            .filterNotNull()
            .toSet()

    return CompleteTournamentInfo(tournamentId = tournamentId,
                                  participants = participants,
                                  winners = winners)
  }

  private fun toRound(roundName: String): Round =
      when (roundName) {
        "Final" -> F
        "Semifinals", "Semifinal" -> SF
        "Quarterfinals", "Quarterfinal" -> QF
        "Fourth Round" -> R4
        "Third Round" -> R3
        "Second Round", "Round of 16" -> R2
        "First Round", "Round of 28", "Round of 32" -> R1
        else -> throw UnexpectedRoundException()
      }

  private fun winners(round: Array<Fixture>): Set<AtpPlayerId> {

    val winnersVsOpponent = round
        .filter { it.Match?.WinningPlayerId != null && it.Match.WinningPlayerId.isNotBlank() }
        .map { it.Match?.WinningPlayerId!! }
        .toSet()

    val winnersVsBye = round
        .filter { it.Match == null && it.Winner != 0 }
        .map { it.Result?.TeamTop?.Player?.PlayerId ?: it.Result?.TeamBottom?.Player?.PlayerId!! }
        .toSet()

    return winnersVsOpponent union winnersVsBye
  }
}
