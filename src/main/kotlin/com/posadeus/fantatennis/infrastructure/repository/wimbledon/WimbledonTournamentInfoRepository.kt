package com.posadeus.fantatennis.infrastructure.repository.wimbledon

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.Round.*
import com.posadeus.fantatennis.infrastructure.client.wimbledon.WimbledonClient
import com.posadeus.fantatennis.infrastructure.client.wimbledon.model.*
import com.posadeus.fantatennis.infrastructure.repository.exception.UnexpectedRoundException

class WimbledonTournamentInfoRepository(private val client: WimbledonClient) : TournamentInfoRepository {

  override fun canProcess(tournamentId: Int): Boolean =
      tournamentId == 540

  override fun retrieveTournamentInfo(tournamentId: Int, year: Int): TournamentInfo =
      when (val wimbledonResponse = client.retrieveDraws(year)) {

        is WimbledonOkResponse -> convert(tournamentId, wimbledonResponse)
        is WimbledonErrorResponse -> ErrorTournamentInfo
      }

  private fun convert(tournamentId: Int, wimbledonResponse: WimbledonOkResponse): CompleteTournamentInfo {

    val participants = wimbledonResponse.matches
        .filter { it.roundNameShort == "1R" }
        .map { Pair(toAtpId(it.team1.idA), toAtpId(it.team2.idA)).toList() }
        .flatten()
        .toSet()

    val winners = wimbledonResponse.matches
        .map { Pair(toRound(it.roundNameShort), chooseWinner(it)) }
        .groupBy { it.first }
        .mapValues { entry -> entry.value
            .mapNotNull { it.second }
            .map { toAtpId(it) }
            .toSet()
        }

    return CompleteTournamentInfo(tournamentId = tournamentId,
                                  participants = participants,
                                  winners = winners)
  }

  private fun toRound(roundNameShort: String): Round =
      when (roundNameShort) {
        "F" -> F
        "SF" -> SF
        "QF" -> QF
        "4R", "R4" -> R4
        "3R", "R3" -> R3
        "2R", "R2" -> R2
        "1R", "R1" -> R1
        else -> throw UnexpectedRoundException("Round not found: $roundNameShort")
      }

  private fun chooseWinner(wimbledonMatch: WimbledonMatch): String? =
      when (wimbledonMatch.winner) {
        "1" -> wimbledonMatch.team1.idA
        "2" -> wimbledonMatch.team2.idA
        else -> null
      }

  private fun toAtpId(wimbledonId: String): String =
      wimbledonId.removePrefix("atp").uppercase()
}
