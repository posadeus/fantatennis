package com.posadeus.fantatennis.infrastructure.repository.rolandgarros

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.CompleteTournamentInfo
import com.posadeus.fantatennis.domain.model.ErrorTournamentInfo
import com.posadeus.fantatennis.domain.model.Round.R1
import com.posadeus.fantatennis.domain.model.Round.R2
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.RolandGarrosClient
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosErrorResponse
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosMatchBuilder.Companion.aRolandGarrosMatch
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosMatchDataBuilder.Companion.aRolandGarrosMatchData
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosOkResponse
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosOkResponseBuilder.Companion.aRolandGarrosOkResponse
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosPlayerBuilder.Companion.aRolandGarrosPlayer
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosRoundNavBuilder.Companion.aRolandGarrosRoundNav
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosRoundResultBuilder.Companion.aRolandGarrosRoundResult
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosTeamBuilder.Companion.aRolandGarrosTeam
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosTournamentEventBuilder.Companion.aRolandGarrosTournamentEvent
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RolandGarrosTournamentInfoRepositoryTest {

  private val client: RolandGarrosClient = mockk()

  private val repository: TournamentInfoRepository = RolandGarrosTournamentInfoRepository(client)

  @Test
  fun `can process`() {

    assertTrue { repository.canProcess(520) }
  }

  @Test
  fun `cannot process`() {

    assertFalse { repository.canProcess(NOT_520_TOURNAMENT_ID) }
  }

  @Test
  fun `get tournament information`() {

    val clientResponse = aClientResponseWith("First Round",
                                             1111L,
                                             2222L,
                                             3333L,
                                             4444L,
                                             5555L,
                                             6666L,
                                             7777L,
                                             8888L,
                                             "Second Round",
                                             1111L,
                                             2222L,
                                             3333L,
                                             4444L)

    val participants = setOf("1111", "5555", "6666", "2222", "3333", "7777", "8888", "4444")
    val expected = CompleteTournamentInfo(tournamentId = 520,
                                          participants = participants,
                                          winners = mapOf(R2 to setOf("1111"),
                                                          R1 to setOf("1111", "2222", "3333", "4444")))

    every { client.retrieveDraws(A_YEAR) } returns clientResponse

    assertThat(repository.retrieveTournamentInfo(520, A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `tournament error from client`() {

    val expected = ErrorTournamentInfo

    every { client.retrieveDraws(A_YEAR) } returns RolandGarrosErrorResponse

    assertThat(repository.retrieveTournamentInfo(520, A_YEAR)).isEqualTo(expected)
  }

  private fun aClientResponseWith(firstRoundId: String,
                                  firstRoundWinner1: Long,
                                  firstRoundWinner2: Long,
                                  firstRoundWinner3: Long,
                                  firstRoundWinner4: Long,
                                  firstRoundLoser1: Long,
                                  firstRoundLoser2: Long,
                                  firstRoundLoser3: Long,
                                  firstRoundLoser4: Long,
                                  secondRoundId: String,
                                  secondRoundWinner: Long,
                                  secondRoundLoser: Long,
                                  secondRoundPlayer1: Long,
                                  secondRoundPlayer2: Long): RolandGarrosOkResponse =
      aRolandGarrosOkResponse()
          .withTournamentEvent(aRolandGarrosTournamentEventWith(firstRoundId,
                                                                secondRoundId,
                                                                firstRoundWinner1,
                                                                firstRoundWinner2,
                                                                firstRoundWinner3,
                                                                firstRoundWinner4,
                                                                firstRoundLoser1,
                                                                firstRoundLoser2,
                                                                firstRoundLoser3,
                                                                firstRoundLoser4,
                                                                secondRoundWinner,
                                                                secondRoundLoser,
                                                                secondRoundPlayer1,
                                                                secondRoundPlayer2))
          .build()

  private fun aRolandGarrosTournamentEventWith(firstRoundId: String,
                                               secondRoundId: String,
                                               firstRoundWinner1: Long,
                                               firstRoundWinner2: Long,
                                               firstRoundWinner3: Long,
                                               firstRoundWinner4: Long,
                                               firstRoundLoser1: Long,
                                               firstRoundLoser2: Long,
                                               firstRoundLoser3: Long,
                                               firstRoundLoser4: Long,
                                               secondRoundWinner1: Long,
                                               secondRoundLoser1: Long,
                                               secondRoundPlayer1: Long,
                                               secondRoundPlayer2: Long) =
      aRolandGarrosTournamentEvent()
          .withRoundNavs(arrayOf(aRolandGarrosRoundNav().withLabel(firstRoundId).build(),
                                 aRolandGarrosRoundNav().withLabel(secondRoundId).build()))
          .withRoundResults(arrayOf(aRolandGarrosRoundResult()
                                        .withRoundLabel(firstRoundId)
                                        .withRoundNumber(1)
                                        .withMatches(arrayOf(aRolandGarrosMatchWith(firstRoundWinner1, firstRoundLoser1),
                                                             anotherRolandGarrosMatchWith(firstRoundWinner2, firstRoundLoser2),
                                                             aRolandGarrosMatchWith(firstRoundWinner3, firstRoundLoser3),
                                                             anotherRolandGarrosMatchWith(firstRoundWinner4, firstRoundLoser4)))
                                        .build(),
                                    aRolandGarrosRoundResult()
                                        .withRoundLabel(secondRoundId)
                                        .withRoundNumber(2)
                                        .withMatches(arrayOf(aRolandGarrosMatchWith(secondRoundWinner1, secondRoundLoser1),
                                                             aRolandGarrosIncompleteMatchWith(secondRoundPlayer1, secondRoundPlayer2)))
                                        .build()))
          .build()

  private fun aRolandGarrosMatchWith(winner: Long, loser: Long) =
      aRolandGarrosMatch()
          .withMatchData(aRolandGarrosMatchData().withStatusLabel("Completed").build())
          .withTeamA(aRolandGarrosTeam().withPlayers(arrayOf(aRolandGarrosPlayer().withId(winner).build())).withWinner(true).build())
          .withTeamB(aRolandGarrosTeam().withPlayers(arrayOf(aRolandGarrosPlayer().withId(loser).build())).withWinner(false).build())
          .build()

  private fun anotherRolandGarrosMatchWith(winner: Long, loser: Long) =
      aRolandGarrosMatch()
          .withMatchData(aRolandGarrosMatchData().withStatusLabel("Completed").build())
          .withTeamA(aRolandGarrosTeam().withPlayers(arrayOf(aRolandGarrosPlayer().withId(loser).build())).withWinner(false).build())
          .withTeamB(aRolandGarrosTeam().withPlayers(arrayOf(aRolandGarrosPlayer().withId(winner).build())).withWinner(true).build())
          .build()

  private fun aRolandGarrosIncompleteMatchWith(winner: Long, loser: Long) =
      aRolandGarrosMatch()
          .withMatchData(aRolandGarrosMatchData().withStatusLabel(null).build())
          .withTeamA(aRolandGarrosTeam().withPlayers(arrayOf(aRolandGarrosPlayer().withId(winner).build())).withWinner(false).build())
          .withTeamB(aRolandGarrosTeam().withPlayers(arrayOf(aRolandGarrosPlayer().withId(loser).build())).withWinner(false).build())
          .build()

  companion object {

    private const val NOT_520_TOURNAMENT_ID = 123
    private const val A_YEAR = 2025
  }
}