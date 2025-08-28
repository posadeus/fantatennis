package com.posadeus.fantatennis.infrastructure.repository.rolandgarros

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.Round.*
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.RolandGarrosClient
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.*
import com.posadeus.fantatennis.infrastructure.repository.exception.UnexpectedRoundException

class RolandGarrosTournamentInfoRepository(private val client: RolandGarrosClient) : TournamentInfoRepository {

  override fun canProcess(tournamentId: Int): Boolean =
      tournamentId == 520

  override fun retrieveTournamentInfo(tournamentId: Int, year: Int): TournamentInfo =
      when (val rolandGarrosResponse = client.retrieveDraws(year)) {

        is RolandGarrosOkResponse -> convert(tournamentId, rolandGarrosResponse)
        is RolandGarrosErrorResponse -> ErrorTournamentInfo
      }

  private fun convert(tournamentId: Int, rolandGarrosResponse: RolandGarrosOkResponse): TournamentInfo {

    val rounds = rolandGarrosResponse.tournamentEvent
        .roundNavs
        .map { it.label }

    val participants = rolandGarrosResponse.tournamentEvent
        .roundResults
        .filter { rounds.first() == it.roundLabel }
        .flatMap { roundResult ->
          roundResult.matches
              .flatMap { listOf(it.teamA.players.first().id.toString(), it.teamB.players.first().id.toString()) }
        }
        .toSet()

    val winners = rolandGarrosResponse.tournamentEvent
        .roundResults
        .associate { roundResult ->
          convertRound(roundResult.roundNumber) to findMatchWinner(roundResult)
        }

    return CompleteTournamentInfo(tournamentId = tournamentId,
                                  participants = participants,
                                  winners = winners)
  }

  private fun findMatchWinner(roundResult: RolandGarrosRoundResult): Set<String> =
      roundResult.matches
          .filter { it.matchData.statusLabel == "Completed" }
          .map {
            if (it.teamA.winner) it.teamA.players.first().id.toString()
            else it.teamB.players.first().id.toString()
          }
          .toSet()

  private fun convertRound(roundNumber: Int): Round =
      when (roundNumber) {
        1 -> R1
        2 -> R2
        3 -> R3
        4 -> R4
        5 -> QF
        6 -> SF
        7 -> F
        else -> throw UnexpectedRoundException("Round not found: $roundNumber")
      }
}
