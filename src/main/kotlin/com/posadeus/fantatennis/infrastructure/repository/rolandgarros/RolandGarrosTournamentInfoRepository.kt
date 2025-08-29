package com.posadeus.fantatennis.infrastructure.repository.rolandgarros

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.Round.*
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.RolandGarrosClient
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.*
import com.posadeus.fantatennis.infrastructure.repository.exception.RolandGarrosPlayerNotFoundException
import com.posadeus.fantatennis.infrastructure.repository.exception.UnexpectedRoundException
import java.math.BigDecimal

class RolandGarrosTournamentInfoRepository(private val client: RolandGarrosClient,
                                           private val playersRepository: RetrievePlayersRepository) : TournamentInfoRepository {

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

    val allPlayers = playersRepository.retrieve()
        .filter { it.rolandGarrosId != null }
        .associateBy { it.rolandGarrosId!! }

    val participants = rolandGarrosResponse.tournamentEvent
        .roundResults
        .filter { rounds.first() == it.roundLabel }
        .flatMap { roundResult ->
          roundResult.matches
              .flatMap {
                listOf(toAtpTourId(it.teamA.players.first().id, allPlayers),
                       toAtpTourId(it.teamB.players.first().id, allPlayers))
              }
              .filterNotNull()
        }
        .toSet()

    val winners = rolandGarrosResponse.tournamentEvent
        .roundResults
        .associate { roundResult ->
          convertRound(roundResult.roundNumber) to findMatchWinner(roundResult, allPlayers)
        }

    return CompleteTournamentInfo(tournamentId = tournamentId,
                                  participants = participants,
                                  winners = winners)
  }

  private fun toAtpTourId(id: Long, allPlayers: Map<BigDecimal, DomainPlayer>): String =
      allPlayers[id.toBigDecimal()]?.atpId
      ?: throw RolandGarrosPlayerNotFoundException("Player not found: $id")

  private fun findMatchWinner(roundResult: RolandGarrosRoundResult, allPlayers: Map<BigDecimal, DomainPlayer>): Set<String> =
      roundResult.matches
          .filter { it.matchData.statusLabel == "Completed" }
          .map {
            if (it.teamA.winner) toAtpTourId(it.teamA.players.first().id, allPlayers)
            else toAtpTourId(it.teamB.players.first().id, allPlayers)
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
