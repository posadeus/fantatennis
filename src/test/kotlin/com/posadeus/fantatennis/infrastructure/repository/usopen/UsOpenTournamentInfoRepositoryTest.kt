package com.posadeus.fantatennis.infrastructure.repository.usopen

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.CompleteTournamentInfo
import com.posadeus.fantatennis.domain.model.ErrorTournamentInfo
import com.posadeus.fantatennis.domain.model.Round.R1
import com.posadeus.fantatennis.domain.model.Round.R2
import com.posadeus.fantatennis.infrastructure.client.usopen.UsOpenClient
import com.posadeus.fantatennis.infrastructure.client.usopen.model.*
import com.posadeus.fantatennis.infrastructure.client.usopen.model.UsOpenMatchBuilder.Companion.aUsOpenMatch
import com.posadeus.fantatennis.infrastructure.client.usopen.model.UsOpenOkResponseBuilder.Companion.aUsOpenOkResponse
import com.posadeus.fantatennis.infrastructure.client.usopen.model.UsOpenTeamBuilder.Companion.aUsOpenTeam
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UsOpenTournamentInfoRepositoryTest {

  private val client: UsOpenClient = mockk()

  private val repository: TournamentInfoRepository = UsOpenTournamentInfoRepository(client)

  @Test
  fun `can process`() {

    assertTrue { repository.canProcess(560) }
  }

  @Test
  fun `cannot process`() {

    assertFalse { repository.canProcess(NOT_560_TOURNAMENT_ID) }
  }

  @Test
  fun `get tournament information`() {

    val clientResponse = aClientResponseWith("R1",
                                             "atpA_WINNER_id",
                                             "atpANOTHER_WINNER_ID",
                                             "atpFIRST_ROUND_LOSER_1",
                                             "atpFIRST_ROUND_LOSER_2",
                                             "R2",
                                             "atpA_WINNER_ID",
                                             "atpANOTHER_WINNER_ID")

    val participants = setOf("A_WINNER_ID", "FIRST_ROUND_LOSER_1", "FIRST_ROUND_LOSER_2", "ANOTHER_WINNER_ID")
    val expected = CompleteTournamentInfo(tournamentId = 560,
                                          participants = participants,
                                          winners = mapOf(R2 to setOf("A_WINNER_ID"),
                                                          R1 to setOf("A_WINNER_ID", "ANOTHER_WINNER_ID")))

    every { client.retrieveDraws(A_YEAR) } returns clientResponse

    assertThat(repository.retrieveTournamentInfo(560, A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `tournament error from client`() {

    val expected = ErrorTournamentInfo

    every { client.retrieveDraws(A_YEAR) } returns UsOpenErrorResponse

    assertThat(repository.retrieveTournamentInfo(560, A_YEAR)).isEqualTo(expected)
  }

  private fun aClientResponseWith(firstRoundId: String,
                                  firstRoundWinner1: String,
                                  firstRoundWinner2: String,
                                  firstRoundLoser1: String,
                                  firstRoundLoser2: String,
                                  secondRoundId: String,
                                  secondRoundWinner: String,
                                  secondRoundLoser: String): UsOpenOkResponse =
      aUsOpenOkResponse()
          .withMatches(arrayOf(aUsOpenMatchWith(firstRoundId, firstRoundWinner1, firstRoundLoser1),
                               anotherUsOpenMatchWith(firstRoundId, firstRoundWinner2, firstRoundLoser2),
                               aUsOpenMatchWith(secondRoundId, secondRoundWinner, secondRoundLoser)))
          .build()

  private fun aUsOpenMatchWith(roundNameShort: String,
                               winnerId: String,
                               loserId: String): UsOpenMatch =
      aUsOpenMatch()
          .withRoundNameShort(roundNameShort)
          .withWinner("1")
          .withTeam1(aUsOpenTeam()
                         .withIdA(winnerId)
                         .withWon(true)
                         .build())
          .withTeam2(aUsOpenTeam()
                         .withIdA(loserId)
                         .withWon(false)
                         .build())
          .build()

  private fun anotherUsOpenMatchWith(roundNameShort: String,
                                     winnerId: String,
                                     loserId: String): UsOpenMatch =
      aUsOpenMatch()
          .withRoundNameShort(roundNameShort)
          .withWinner("2")
          .withTeam1(aUsOpenTeam()
                         .withIdA(loserId)
                         .withWon(false)
                         .build())
          .withTeam2(aUsOpenTeam()
                         .withIdA(winnerId)
                         .withWon(true)
                         .build())
          .build()

  companion object {

    private const val NOT_560_TOURNAMENT_ID = 123
    private const val A_YEAR = 2024
  }
}