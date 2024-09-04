package com.posadeus.fantatennis.infrastructure.repository.tennistv

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.client.tennistv.TennisTvClient
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.TennisTvTournamentErrorResponse
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.TennisTvTournamentOkResponse

class TennisTvTournamentInfoRepository(private val client: TennisTvClient) : TournamentInfoRepository {

  override fun retrieveTournamentInfo(tournamentId: Int, year: Int): TournamentInfo =
      when (val response = client.retrieveTournamentInfo(tournamentId, year)) {

        is TennisTvTournamentOkResponse -> toTournamentInfo(tournamentId, response)
        is TennisTvTournamentErrorResponse -> ErrorTournamentInfo
      }

  private fun toTournamentInfo(tournamentId: Int, response: TennisTvTournamentOkResponse): CompleteTournamentInfo {

    val tournament = response.tournament.MS

    val winners = tournament.Rounds
        .associate { round ->
          (round.RoundName
              to round.Fixtures
              .filter { it.Match?.WinningPlayerId != null && it.Match.WinningPlayerId.isNotBlank() }
              .map { it.Match?.WinningPlayerId!! }
              .toSet())
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
}
