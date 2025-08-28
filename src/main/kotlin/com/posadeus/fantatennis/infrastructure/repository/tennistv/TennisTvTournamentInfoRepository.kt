package com.posadeus.fantatennis.infrastructure.repository.tennistv

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.Round
import com.posadeus.fantatennis.domain.model.Round.*
import com.posadeus.fantatennis.infrastructure.client.tennistv.TennisTvClient
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.*
import com.posadeus.fantatennis.infrastructure.repository.exception.UnexpectedRoundException
import com.posadeus.fantatennis.infrastructure.repository.exception.UnexpectedTournamentFormatException

class TennisTvTournamentInfoRepository(private val client: TennisTvClient) : TournamentInfoRepository {

  override fun canProcess(tournamentId: Int): Boolean =
      true

  override fun retrieveTournamentInfo(tournamentId: Int, year: Int): TournamentInfo =
      when (val response = client.retrieveTournamentInfo(tournamentId, year)) {

        is TennisTvTournamentOkResponse -> toTournamentInfo(tournamentId, response, year)
        is TennisTvTournamentErrorResponse -> ErrorTournamentInfo
      }

  private fun toTournamentInfo(tournamentId: Int, response: TennisTvTournamentOkResponse, year: Int): CompleteTournamentInfo {

    val tournament = response.tournament.MS
    val totalPlayers = tournament.DrawSize + tournament.NumByes

    val winners = tournament.Rounds
        .associate { round ->
          (toRound(round.RoundIdModernized, totalPlayers)
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

  private fun toRound(roundId: Int, totalPlayers: Int): Round =

      when (totalPlayers) {
        128 -> to128Tournament(roundId)
        64 -> to64Tournament(roundId)
        32 -> to32Tournament(roundId)
        16 -> to16Tournament(roundId)
        else -> throw UnexpectedTournamentFormatException("Tournament format not found: $totalPlayers")
      }

  private fun to16Tournament(roundId: Int): Round =
      when (roundId) {
        7 -> F
        6 -> SF
        5 -> QF
        4 -> R1
        else -> throw UnexpectedRoundException("Round not found: $roundId")
      }

  private fun to32Tournament(roundId: Int): Round =
      when (roundId) {
        7 -> F
        6 -> SF
        5 -> QF
        4 -> R2
        3 -> R1
        else -> throw UnexpectedRoundException("Round not found: $roundId")
      }

  private fun to64Tournament(roundId: Int): Round =
      when (roundId) {
        7 -> F
        6 -> SF
        5 -> QF
        4 -> R3
        3 -> R2
        2 -> R1
        else -> throw UnexpectedRoundException("Round not found: $roundId")
      }

  private fun to128Tournament(roundId: Int): Round =
      when (roundId) {
        7 -> F
        6 -> SF
        5 -> QF
        4 -> R4
        3 -> R3
        2 -> R2
        1 -> R1
        else -> throw UnexpectedRoundException("Round not found: $roundId")
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
