package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.domain.infrastructure.TournamentRepository
import com.posadeus.fantatennis.domain.model.CompleteTournamentInfo
import com.posadeus.fantatennis.domain.model.DomainPlayer
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FantaPointCalculatorServiceTest {

  private val tournamentRepository: TournamentRepository = mockk()

  private val service = FantaPointCalculatorService(tournamentRepository)

  @Test
  fun `calculate points for 1000 tournamentType tournament`() {

    val participants = setOf("PlayerId1", "PlayerId2", "PlayerId3", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7")
    val winners = mapOf("Final" to setOf("PlayerId4"),
                        "Semifinals" to setOf("PlayerId1", "PlayerId4"),
                        "Quarterfinals" to setOf("PlayerId1", "PlayerId2", "PlayerId4"),
                        "Third Round" to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5"),
                        "Second Round" to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId7"),
                        "First Round" to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7"))

    val tournamentInfo = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                                tournamentType = "1000",
                                                participants = participants,
                                                winners = winners)

    val expected = setOf(DomainPlayer(id = "PlayerId1",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 28.0))),
                         DomainPlayer(id = "PlayerId2",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 16.0))),
                         DomainPlayer(id = "PlayerId3",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 1.0))),
                         DomainPlayer(id = "PlayerId4",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 40.0))),
                         DomainPlayer(id = "PlayerId5",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 8.0))),
                         DomainPlayer(id = "PlayerId6",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 2.0))),
                         DomainPlayer(id = "PlayerId7",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 4.0))))

    every { tournamentRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns tournamentInfo

    assertThat(service.calculate(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `calculate points for 500 tournamentType tournament`() {

    val participants = setOf("PlayerId1", "PlayerId2", "PlayerId3", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7")
    val winners = mapOf("Final" to setOf("PlayerId4"),
                        "Semifinals" to setOf("PlayerId1", "PlayerId4"),
                        "Quarterfinals" to setOf("PlayerId1", "PlayerId2", "PlayerId4"),
                        "Third Round" to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5"),
                        "Second Round" to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId7"),
                        "First Round" to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7"))

    val tournamentInfo = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                                tournamentType = "500",
                                                participants = participants,
                                                winners = winners)

    val expected = setOf(DomainPlayer(id = "PlayerId1",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 14.0))),
                         DomainPlayer(id = "PlayerId2",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 8.0))),
                         DomainPlayer(id = "PlayerId3",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 0.0))),
                         DomainPlayer(id = "PlayerId4",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 20.0))),
                         DomainPlayer(id = "PlayerId5",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 4.0))),
                         DomainPlayer(id = "PlayerId6",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 1.0))),
                         DomainPlayer(id = "PlayerId7",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 2.0))))

    every { tournamentRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns tournamentInfo

    assertThat(service.calculate(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `calculate points for 250 tournamentType tournament`() {

    val participants = setOf("PlayerId1", "PlayerId2", "PlayerId3", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7")
    val winners = mapOf("Final" to setOf("PlayerId4"),
                        "Semifinals" to setOf("PlayerId1", "PlayerId4"),
                        "Quarterfinals" to setOf("PlayerId1", "PlayerId2", "PlayerId4"),
                        "Third Round" to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5"),
                        "Second Round" to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId7"),
                        "First Round" to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7"))

    val tournamentInfo = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                                tournamentType = "250",
                                                participants = participants,
                                                winners = winners)

    val expected = setOf(DomainPlayer(id = "PlayerId1",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 7.0))),
                         DomainPlayer(id = "PlayerId2",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 4.0))),
                         DomainPlayer(id = "PlayerId3",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 0.0))),
                         DomainPlayer(id = "PlayerId4",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 10.0))),
                         DomainPlayer(id = "PlayerId5",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 2.0))),
                         DomainPlayer(id = "PlayerId6",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 0.0))),
                         DomainPlayer(id = "PlayerId7",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 1.0))))

    every { tournamentRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns tournamentInfo

    assertThat(service.calculate(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  companion object {

    private const val A_TOURNAMENT_ID = 123
    private const val A_YEAR = 2222
  }
}