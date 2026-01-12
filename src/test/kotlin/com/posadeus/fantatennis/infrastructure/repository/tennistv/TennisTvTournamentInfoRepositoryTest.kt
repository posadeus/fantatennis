package com.posadeus.fantatennis.infrastructure.repository.tennistv

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.CompleteTournamentInfo
import com.posadeus.fantatennis.domain.model.ErrorTournamentInfo
import com.posadeus.fantatennis.domain.model.Round.R1
import com.posadeus.fantatennis.domain.model.Round.R2
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
import com.posadeus.fantatennis.infrastructure.repository.exception.UnexpectedRoundException
import com.posadeus.fantatennis.infrastructure.repository.exception.UnexpectedTournamentFormatException
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

class TennisTvTournamentInfoRepositoryTest {

  private val client: TennisTvClient = mockk()

  private val tournamentInfoRepository: TournamentInfoRepository = TennisTvTournamentInfoRepository(client)

  @Test
  fun `can process`() {

    assertTrue { tournamentInfoRepository.canProcess(ANY_TOURNAMENT_ID) }
  }

  @Test
  fun `get tournament information`() {

    val clientResponse = aClientResponseWith(arrayOf(aRound(4,
                                                            "A_WINNER",
                                                            "ANOTHER_WINNER"),
                                                     aRound(5,
                                                            "A_WINNER",
                                                            "ANOTHER_WINNER",
                                                            "FIRST_ROUND_LOSER_1",
                                                            "FIRST_ROUND_LOSER_2")),
                                             32,
                                             0)
    val participants = setOf("A_WINNER", "ANOTHER_WINNER", "FIRST_ROUND_LOSER_1", "FIRST_ROUND_LOSER_2")

    val expected = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                          participants = participants,
                                          winners = mapOf(R2 to setOf("A_WINNER"),
                                                          R1 to setOf("A_WINNER", "ANOTHER_WINNER")))

    every { client.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns clientResponse

    assertThat(tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `get tournament information - winner against bye`() {

    val clientResponse = aClientResponseWith(arrayOf(aRound(4,
                                                            "A_WINNER",
                                                            "ANOTHER_WINNER"),
                                                     aRound(5,
                                                            "A_WINNER",
                                                            "FIRST_ROUND_LOSER_1",
                                                            aResultTeamPlayer()
                                                                .withPlayerId("ANOTHER_WINNER")
                                                                .build(),
                                                            null)),
                                             32,
                                             0)
    val participants = setOf("A_WINNER", "FIRST_ROUND_LOSER_1", "ANOTHER_WINNER")

    val expected = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                          participants = participants,
                                          winners = mapOf(R2 to setOf("A_WINNER"),
                                                          R1 to setOf("A_WINNER", "ANOTHER_WINNER")))

    every { client.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns clientResponse

    assertThat(tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `get tournament information - match not played`() {

    val clientResponse = aClientResponseWith(arrayOf(aNotPlayedRound(5,
                                                                     null,
                                                                     0,
                                                                     null)),
                                             32,
                                             0)

    val expected = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                          participants = emptySet(),
                                          winners = mapOf(R1 to emptySet()))

    every { client.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns clientResponse

    assertThat(tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `tournament format exception`() {

    val clientResponse = aClientResponseWith(arrayOf(aRound(ANY_ROUND_ID,
                                                            A_FIXTURE_PLAYER,
                                                            A_FIXTURE_PLAYER)),
                                             0,
                                             10)

    every { client.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns clientResponse

    assertThrows<UnexpectedTournamentFormatException> { tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) }
  }

  @Test
  fun `round exception`() {

    val clientResponse = aClientResponseWith(arrayOf(aRound(6,
                                                            A_FIXTURE_PLAYER,
                                                            A_FIXTURE_PLAYER)),
                                             32,
                                             0)

    every { client.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns clientResponse

    assertThrows<UnexpectedRoundException> { tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) }
  }

  @Test
  fun `tournament error from client`() {

    val expected = ErrorTournamentInfo

    every { client.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns TennisTvTournamentErrorResponse

    assertThat(tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  private fun aClientResponseWith(rounds: Array<Round>, drawSize: Int, numOfByes: Int) =
      TennisTvTournamentOkResponseBuilder()
          .withTournament(aTournamentResponse()
                              .withMS(aMS()
                                          .withDrawSize(drawSize)
                                          .withNumByes(numOfByes)
                                          .withBreakdown(arrayOf(aBreakdown()
                                                                     .build()))
                                          .withRounds(rounds)
                                          .build())
                              .build())
          .build()

  private fun aRound(roundId: Int,
                     fixture1Winner: String,
                     fixture2Winner: String,
                     fixture1Loser: String,
                     fixture2Loser: String) =
      aRound()
          .withRoundId(roundId)
          .withFixtures(arrayOf(aFixture(fixture1Winner, fixture1Loser),
                                aFixture(fixture2Winner, fixture2Loser)))
          .build()

  private fun aRound(roundId: Int,
                     fixtureWinner: String,
                     fixtureLoser: String) =
      aRound()
          .withRoundId(roundId)
          .withFixtures(arrayOf(aFixture(fixtureWinner, fixtureLoser)))
          .build()

  private fun aNotPlayedRound(roundId: Int,
                              match: Match?,
                              winner: Int,
                              result: Result?) =
      aRound()
          .withRoundId(roundId)
          .withFixtures(arrayOf(aFixtureNotPlayed(match, winner, result)))
          .build()

  private fun aRound(roundId: Int,
                     fixture1Winner: String,
                     fixture1Loser: String,
                     resultTeamPlayer1: ResultTeamPlayer,
                     resultTeamPlayer2: ResultTeamPlayer?) =
      aRound()
          .withRoundId(roundId)
          .withFixtures(arrayOf(aFixture(fixture1Winner, fixture1Loser),
                                aFixtureWithoutAMatch(resultTeamPlayer1, resultTeamPlayer2)))
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

  private fun aFixtureWithoutAMatch(resultTeamPlayer1: ResultTeamPlayer, resultTeamPlayer2: ResultTeamPlayer?) =
      aFixture()
          .withMatch(null)
          .withWinner(1)
          .withResult(aResult()
                          .withTeamTop(aResultTeam()
                                           .withPlayer(resultTeamPlayer1)
                                           .build())
                          .withTeamBottom(aResultTeam()
                                              .withPlayer(resultTeamPlayer2)
                                              .build())
                          .build())
          .build()

  private fun aFixtureNotPlayed(match: Match?, winner: Int, result: Result?) =
      aFixture()
          .withMatch(match)
          .withWinner(winner)
          .withResult(result)
          .build()

  companion object {

    private const val A_TOURNAMENT_ID = 123
    private const val ANY_TOURNAMENT_ID = 2345
    private const val A_YEAR = 2000
    private const val ANY_ROUND_ID = 1
    private const val A_FIXTURE_PLAYER = "A_FIXTURE_PLAYER"
  }
}