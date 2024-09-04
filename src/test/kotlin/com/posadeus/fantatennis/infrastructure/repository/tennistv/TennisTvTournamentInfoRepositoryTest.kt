package com.posadeus.fantatennis.infrastructure.repository.tennistv

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.CompleteTournamentInfo
import com.posadeus.fantatennis.domain.model.ErrorTournamentInfo
import com.posadeus.fantatennis.infrastructure.client.tennistv.TennisTvClient
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.*
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.BreakdownBuilder.Companion.aBreakdown
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.FixtureBuilder.Companion.aFixture
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.MSBuilder.Companion.aMS
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.MatchBuilder.Companion.aMatch
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.ResultBuilder.Companion.aResult
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.ResultTeamBuilder.Companion.aResultTeam
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.ResultTeamPlayerBuilder.Companion.aResultTeamPlayer
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.RoundBuilder.Companion.aRound
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.TournamentResponseBuilder.Companion.aTournamentResponse
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TennisTvTournamentInfoRepositoryTest {

  private val client: TennisTvClient = mockk()

  private val tournamentInfoRepository: TournamentInfoRepository = TennisTvTournamentInfoRepository(client)

  @Test
  fun `get tournament information`() {

    val clientResponse = aClientResponseWith(arrayOf(aRound("ROUND_2",
                                                            "A_WINNER",
                                                            "ANOTHER_WINNER"),
                                                     aRound("ROUND_1",
                                                            "A_WINNER",
                                                            "ANOTHER_WINNER",
                                                            "FIRST_ROUND_LOSER_1",
                                                            "FIRST_ROUND_LOSER_2")))
    val participants = setOf("A_WINNER", "ANOTHER_WINNER", "FIRST_ROUND_LOSER_1", "FIRST_ROUND_LOSER_2")

    val expected = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                          participants = participants,
                                          winners = mapOf("ROUND_2" to setOf("A_WINNER"),
                                                          "ROUND_1" to setOf("A_WINNER", "ANOTHER_WINNER")))

    every { client.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns clientResponse

    assertThat(tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `tournament error from client`() {

    val expected = ErrorTournamentInfo

    every { client.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns TennisTvTournamentErrorResponse

    assertThat(tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  private fun aClientResponseWith(rounds: Array<Round>) =
      TennisTvTournamentOkResponseBuilder()
          .withTournament(aTournamentResponse()
                              .withMS(aMS()
                                          .withBreakdown(arrayOf(aBreakdown()
                                                                     .build()))
                                          .withRounds(rounds)
                                          .build())
                              .build())
          .build()

  private fun aRound(roundName: String,
                     fixture1Winner: String,
                     fixture2Winner: String,
                     fixture1Loser: String,
                     fixture2Loser: String) =
      aRound()
          .withRoundName(roundName)
          .withFixtures(arrayOf(aFixture(fixture1Winner, fixture1Loser),
                                aFixture(fixture2Winner, fixture2Loser)))
          .build()

  private fun aRound(roundName: String,
                     fixtureWinner: String,
                     fixtureLoser: String) =
      aRound()
          .withRoundName(roundName)
          .withFixtures(arrayOf(aFixture(fixtureWinner, fixtureLoser)))
          .build()

  private fun aFixture(winner: String, loser: String) =
      aFixture()
          .withMatch(aMatch().withWinningPlayerId(winner).build())
          .withResult(aResult()
                          .withTeamTop(aResultTeam()
                                           .withPlayer(aResultTeamPlayer()
                                                           .withPlayerId(winner)
                                                           .build())
                                           .build())
                          .withTeamBottom(aResultTeam()
                                              .withPlayer(aResultTeamPlayer()
                                                              .withPlayerId(loser)
                                                              .build())
                                              .build())
                          .build())
          .build()

  companion object {

    private const val A_TOURNAMENT_ID = 123
    private const val A_YEAR = 2000
  }
}