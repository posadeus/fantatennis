package com.posadeus.fantatennis.infrastructure.repository.wimbledon

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.CompleteTournamentInfo
import com.posadeus.fantatennis.domain.model.TournamentInfo
import com.posadeus.fantatennis.infrastructure.client.wimbledon.WimbledonClient
import com.posadeus.fantatennis.infrastructure.client.wimbledon.model.WimbledonMatch
import com.posadeus.fantatennis.infrastructure.client.wimbledon.model.WimbledonOkResponse

class WimbledonTournamentInfoRepository(private val client: WimbledonClient) : TournamentInfoRepository {

  override fun retrieveTournamentInfo(tournamentId: Int, year: Int): TournamentInfo {

    val wimbledonResponse = client.retrieveDraws(year) as WimbledonOkResponse

    return convert(tournamentId, wimbledonResponse)
  }

  private fun convert(tournamentId: Int, wimbledonResponse: WimbledonOkResponse): CompleteTournamentInfo {

    val participants = wimbledonResponse.matches
        .filter { it.roundNameShort == "1R" }
        .map { Pair(toAtpId(it.team1.idA), toAtpId(it.team2.idA)).toList() }
        .flatten()
        .toSet()

    val winners = wimbledonResponse.matches
        .map { Pair(it.roundNameShort, chooseWinner(it)) }
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

  private fun chooseWinner(wimbledonMatch: WimbledonMatch): String? =
      when (wimbledonMatch.winner) {
        "1" -> wimbledonMatch.team1.idA
        "2" -> wimbledonMatch.team2.idA
        else -> null
      }

  private fun toAtpId(wimbledonId: String): String =
      wimbledonId.removePrefix("atp")
}
