package com.posadeus.fantatennis.infrastructure.repository.wimbledon

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.CompleteTournamentInfo
import com.posadeus.fantatennis.domain.model.ErrorTournamentInfo
import com.posadeus.fantatennis.domain.model.Round.R1
import com.posadeus.fantatennis.domain.model.Round.R2
import com.posadeus.fantatennis.infrastructure.client.wimbledon.WimbledonClient
import com.posadeus.fantatennis.infrastructure.client.wimbledon.model.*
import com.posadeus.fantatennis.infrastructure.client.wimbledon.model.WimbledonMatchBuilder.Companion.aWimbledonMatch
import com.posadeus.fantatennis.infrastructure.client.wimbledon.model.WimbledonOkResponseBuilder.Companion.aWimbledonOkResponse
import com.posadeus.fantatennis.infrastructure.client.wimbledon.model.WimbledonTeamBuilder.Companion.aWimbledonTeam
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WimbledonTournamentInfoRepositoryTest {

  private val client: WimbledonClient = mockk()

  private val repository: TournamentInfoRepository = WimbledonTournamentInfoRepository(client)

  @Test
  fun `get tournament information`() {

    val clientResponse = aClientResponseWith("1R",
                                             "atpA_WINNER_ID",
                                             "atpANOTHER_WINNER_ID",
                                             "atpFIRST_ROUND_LOSER_1",
                                             "atpFIRST_ROUND_LOSER_2",
                                             "2R",
                                             "atpA_WINNER_ID",
                                             "atpANOTHER_WINNER_ID")

    val participants = setOf("A_WINNER_ID", "FIRST_ROUND_LOSER_1", "FIRST_ROUND_LOSER_2", "ANOTHER_WINNER_ID")
    val expected = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                          participants = participants,
                                          winners = mapOf(R2 to setOf("A_WINNER_ID"),
                                                          R1 to setOf("A_WINNER_ID", "ANOTHER_WINNER_ID")))

    every { client.retrieveDraws(A_YEAR) } returns clientResponse

    assertThat(repository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `tournament error from client`() {

    val expected = ErrorTournamentInfo

    every { client.retrieveDraws(A_YEAR) } returns WimbledonErrorResponse

    assertThat(repository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  private fun aClientResponseWith(firstRoundId: String,
                                  firstRoundWinner1: String,
                                  firstRoundWinner2: String,
                                  firstRoundLoser1: String,
                                  firstRoundLoser2: String,
                                  secondRoundId: String,
                                  secondRoundWinner: String,
                                  secondRoundLoser: String): WimbledonOkResponse =
      aWimbledonOkResponse()
          .withMatches(arrayOf(aWimbledonMatchWith(firstRoundId, firstRoundWinner1, firstRoundLoser1),
                               anotherWimbledonMatchWith(firstRoundId, firstRoundWinner2, firstRoundLoser2),
                               aWimbledonMatchWith(secondRoundId, secondRoundWinner, secondRoundLoser)))
          .build()

  private fun aWimbledonMatchWith(roundNameShort: String,
                                  winnerId: String,
                                  loserId: String): WimbledonMatch =
      aWimbledonMatch()
          .withRoundNameShort(roundNameShort)
          .withWinner("1")
          .withTeam1(aWimbledonTeam()
                         .withIdA(winnerId)
                         .withWon(true)
                         .build())
          .withTeam2(aWimbledonTeam()
                         .withIdA(loserId)
                         .withWon(false)
                         .build())
          .build()

  private fun anotherWimbledonMatchWith(roundNameShort: String,
                                        winnerId: String,
                                        loserId: String): WimbledonMatch =
      aWimbledonMatch()
          .withRoundNameShort(roundNameShort)
          .withWinner("2")
          .withTeam1(aWimbledonTeam()
                         .withIdA(loserId)
                         .withWon(false)
                         .build())
          .withTeam2(aWimbledonTeam()
                         .withIdA(winnerId)
                         .withWon(true)
                         .build())
          .build()

  companion object {

    private const val A_TOURNAMENT_ID = 123
    private const val A_YEAR = 2024
  }
}