package com.posadeus.fantatennis.infrastructure.repository.ausopen

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.Round.*
import com.posadeus.fantatennis.infrastructure.client.ausopen.AusOpenClient
import com.posadeus.fantatennis.infrastructure.client.ausopen.model.*
import com.posadeus.fantatennis.infrastructure.repository.exception.UnexpectedRoundException

typealias TeamUUID = String
typealias PlayerUUID = String
typealias AtpPlayerId = String

class AusOpenTournamentInfoRepository(private val client: AusOpenClient,
                                      private val tournamentEventIdService: AusOpenTournamentEventIdService)
  : TournamentInfoRepository {

  override fun canProcess(tournamentId: Int): Boolean =
      tournamentId == 580

  override fun retrieveTournamentInfo(tournamentId: Int, year: Int): TournamentInfo =
      when (val ausOpenResponse = client.retrieveDraws(tournamentEventIdService.retrieveEventId(year))) {

        is AusOpenOkResponse -> convert(tournamentId, ausOpenResponse)
        is AusOpenErrorResponse -> ErrorTournamentInfo
      }

  private fun convert(tournamentId: Int, ausOpenResponse: AusOpenOkResponse): CompleteTournamentInfo {

    val roundNamesByUUID = ausOpenResponse.rounds
        .associate { it.uuid to it.name }

    val teamsByUUID = ausOpenResponse.teams
        .associate { it.uuid to it.players.first() }

    val playersByUUID = ausOpenResponse.players
        .associate { it.uuid to toAtpId(it.tour_id) }

    val participants = playersByUUID
        .map { it.value }
        .toSet()

    val winners =
        ausOpenResponse.matches
            .map { Pair(toRound(it.round_id, roundNamesByUUID), chooseWinner(it, teamsByUUID, playersByUUID)) }
            .groupBy { it.first }
            .mapValues { entry ->
              entry.value
                  .mapNotNull { it.second }
                  .toSet()
            }

    return CompleteTournamentInfo(tournamentId = tournamentId,
                                  participants = participants,
                                  winners = winners)
  }

  private fun chooseWinner(match: AusOpenMatch,
                           teams: Map<TeamUUID, PlayerUUID>,
                           players: Map<PlayerUUID, AtpPlayerId>): String? =
      match.teams
          .firstOrNull { it.status == "Winner" }
          ?.team_id
          ?.let { players[teams[it]] }

  private fun toRound(roundUUID: String, roundNamesByUUID: Map<String, String>): Round =
      when (roundNamesByUUID[roundUUID]) {
        "Final" -> F
        "Semifinals" -> SF
        "Quarterfinals" -> QF
        "4th Round" -> R4
        "3rd Round" -> R3
        "2nd Round" -> R2
        "1st Round" -> R1
        else -> throw UnexpectedRoundException()
      }

  private fun toAtpId(ausOpenId: String): String =
      ausOpenId.removePrefix("ATP").uppercase()
}
