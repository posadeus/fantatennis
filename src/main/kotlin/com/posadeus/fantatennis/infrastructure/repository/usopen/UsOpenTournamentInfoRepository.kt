package com.posadeus.fantatennis.infrastructure.repository.usopen

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.Round.*
import com.posadeus.fantatennis.infrastructure.client.usopen.UsOpenClient
import com.posadeus.fantatennis.infrastructure.client.usopen.model.*
import com.posadeus.fantatennis.infrastructure.repository.exception.UnexpectedRoundException

class UsOpenTournamentInfoRepository(private val client: UsOpenClient) : TournamentInfoRepository {

  override fun canProcess(tournamentId: Int): Boolean =
      tournamentId == 560

  override fun retrieveTournamentInfo(tournamentId: Int, year: Int): TournamentInfo =
      when (val usOpenResponse = client.retrieveDraws(year)) {

        is UsOpenOkResponse -> convert(tournamentId, usOpenResponse)
        is UsOpenErrorResponse -> ErrorTournamentInfo
      }

  private fun convert(tournamentId: Int, usOpenResponse: UsOpenOkResponse): CompleteTournamentInfo {

    val participants = usOpenResponse.matches
        .filter { it.roundNameShort == "R1" }
        .map { Pair(toAtpId(it.team1.idA), toAtpId(it.team2.idA)).toList() }
        .flatten()
        .toSet()

    val winners = usOpenResponse.matches
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
        "R4" -> R4
        "R3" -> R3
        "R2" -> R2
        "R1" -> R1
        else -> throw UnexpectedRoundException("Round not found: $roundNameShort")
      }

  private fun chooseWinner(usOpenMatch: UsOpenMatch): String? =
      when (usOpenMatch.winner) {
        "1" -> usOpenMatch.team1.idA
        "2" -> usOpenMatch.team2.idA
        else -> null
      }

  private fun toAtpId(usOpenId: String): String =
      usOpenId.removePrefix("atp").uppercase()
}
