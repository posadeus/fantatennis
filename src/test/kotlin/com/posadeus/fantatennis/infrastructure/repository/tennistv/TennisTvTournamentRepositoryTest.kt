package com.posadeus.fantatennis.infrastructure.repository.tennistv

import com.posadeus.fantatennis.domain.infrastructure.TournamentRepository
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

class TennisTvTournamentRepositoryTest {

  private val client: TennisTvClient = mockk()

  private val tournamentRepository: TournamentRepository = TennisTvTournamentRepository(client)

  @Test
  fun `get tournament information`() {

    val clientResponse = aClientResponseWith(A_TOURNAMENT_TYPE,
                                             arrayOf(aRound("ROUND_2",
                                                            "A_WINNER",
                                                            "ANOTHER_WINNER"),
                                                     aRound("ROUND_1",
                                                            "A_WINNER",
                                                            "ANOTHER_WINNER",
                                                            "FIRST_ROUND_LOSER_1",
                                                            "FIRST_ROUND_LOSER_2")))
    val participants = setOf("A_WINNER", "ANOTHER_WINNER", "FIRST_ROUND_LOSER_1", "FIRST_ROUND_LOSER_2")

    val expected = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                          tournamentType = A_TOURNAMENT_TYPE,
                                          participants = participants,
                                          winners = mapOf("ROUND_2" to setOf("A_WINNER"),
                                                          "ROUND_1" to setOf("A_WINNER", "ANOTHER_WINNER")))

    every { client.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns clientResponse

    assertThat(tournamentRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `tournament error from client`() {

    val expected = ErrorTournamentInfo

    every { client.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns TennisTvTournamentErrorResponse

    assertThat(tournamentRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  private fun aClientResponseWith(tournamentPoints: String,
                                  rounds: Array<Round>) =
      TennisTvTournamentOkResponseBuilder()
          .withTournament(aTournamentResponse()
                              .withMS(aMS()
                                          .withBreakdown(arrayOf(aBreakdown()
                                                                     .withPoints(tournamentPoints)
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
    private const val A_TOURNAMENT_TYPE = "A_TOURNAMENT_TYPE"
  }
}



//  @Test
//  fun name1() {
//    val scoresRules1000 = mapOf("Winner" to 40,
//                                "RunnerUp" to 28,
//                                "Semifinals" to 16,
//                                "Quarterfinals" to 8,
//                                "Third Round" to 4,
//                                "Second Round" to 2,
//                                "First Round" to 1)
//    val scoresRules500 = mapOf("Winner" to 20,
//                               "RunnerUp" to 14,
//                               "Semifinals" to 8,
//                               "Quarterfinals" to 4,
//                               "Third Round" to 2,
//                               "Second Round" to 1,
//                               "First Round" to 0)
//    val scoresRules250 = mapOf("Winner" to 10,
//                               "RunnerUp" to 7,
//                               "Semifinals" to 4,
//                               "Quarterfinals" to 2,
//                               "Third Round" to 1,
//                               "Second Round" to 0,
//                               "First Round" to 0)
//
//    val fileContent = this::class.java.classLoader.getResource("test.json")!!.readText()
////    val jsonResponse = Gson().fromJson(fileContent, Response::class.java)
////
////    val tournament250Points = TournamentPointsCalculator().calculatePoints(jsonResponse, scoresRules250)
////    val tournament500Points = TournamentPointsCalculator().calculatePoints(jsonResponse, scoresRules500)
////    val tournament1000Points = TournamentPointsCalculator().calculatePoints(jsonResponse, scoresRules1000)
//  }

//class TournamentPointsCalculator {
//
//  fun calculatePoints(jsonResponse: TournamentResponse, scoresRules: Map<String, Int>): Map<String, Int> {
//
//    val winners = jsonResponse.MS.Rounds
//        .associate { round ->
//          (round.RoundName
//              to round.Fixtures
//              .filter { it.Match?.WinningPlayerId != null && it.Match.WinningPlayerId.isNotBlank() }
//              .map { it.Match?.WinningPlayerId })
//        }
//
//    println()
//    println("winners: $winners")
//
//    val participants = // FIXME not useful for other then 1000
//        jsonResponse.MS.Rounds.last().Fixtures
//            .flatMap { Pair(it.Result.TeamTop.Player?.PlayerId, it.Result.TeamBottom.Player?.PlayerId).toList() }
//            .filterNotNull()
//
//    val scoresFirstRound = participants.associateWith { scoresRules["First Round"]!! }
//    val scoresSecondRound = winners["First Round"]!!.associate { it!! to scoresRules["Second Round"]!! }
//    val scoresThirdRound = winners["Second Round"]!!.associate { it!! to scoresRules["Third Round"]!! }
//    val scoresQuarterfinals = winners["Third Round"]!!.associate { it!! to scoresRules["Quarterfinals"]!! }
//    val scoresSemifinals = winners["Quarterfinals"]!!.associate { it!! to scoresRules["Semifinals"]!! }
//    val scoresRunnerUp = winners["Semifinals"]!!.associate { it!! to scoresRules["RunnerUp"]!! }
//    val scoresWinner = winners["Final"]!!.associate { it!! to scoresRules["Winner"]!! }
//
//    val scoresByPlayer =
//        scoresFirstRound + scoresSecondRound + scoresThirdRound + scoresQuarterfinals + scoresSemifinals + scoresRunnerUp + scoresWinner
//
//    println()
//    println("Scores  $scoresByPlayer")
//
//    return scoresByPlayer
//  }
//}