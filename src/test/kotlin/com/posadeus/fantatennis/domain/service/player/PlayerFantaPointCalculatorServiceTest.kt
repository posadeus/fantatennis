package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.infrastructure.TournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.Round.*
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlayerFantaPointCalculatorServiceTest {

  private val tournamentInfoRepository: TournamentInfoRepository = mockk()
  private val anotherTournamentInfoRepository: TournamentInfoRepository = mockk()
  private val tournamentsRepository: TournamentsRepository = mockk()

  private val service = PlayerFantaPointCalculatorService(listOf(tournamentInfoRepository, anotherTournamentInfoRepository),
                                                          tournamentsRepository)

  @Test
  fun `calculate points for 1000 points tournament with tournamentInfoRepository`() {

    val participants = setOf("PlayerId1", "PlayerId2", "PlayerId3", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7")
    val winners = mapOf(F to setOf("PlayerId4"),
                        SF to setOf("PlayerId1", "PlayerId4"),
                        QF to setOf("PlayerId1", "PlayerId2", "PlayerId4"),
                        R3 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5"),
                        R2 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId7"),
                        R1 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7"))

    val tournaments = listOf(Tournament(id = AN_ID,
                                        tennisTvId = A_TOURNAMENT_ID,
                                        points = 1000))
    val tournamentInfo = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                                participants = participants,
                                                winners = winners)

    val expected = setOf(AtpPlayer(id = "PlayerId1",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 28.0))),
                         AtpPlayer(id = "PlayerId2",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 16.0))),
                         AtpPlayer(id = "PlayerId3",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 1.0))),
                         AtpPlayer(id = "PlayerId4",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 40.0))),
                         AtpPlayer(id = "PlayerId5",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 8.0))),
                         AtpPlayer(id = "PlayerId6",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 2.0))),
                         AtpPlayer(id = "PlayerId7",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 4.0))))

    every { tournamentsRepository.readTournaments() } returns tournaments
    every { tournamentInfoRepository.canProcess(A_TOURNAMENT_ID) } returns true
    every { tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns tournamentInfo

    assertThat(service.calculateFantaPointsFor(AN_ID, A_YEAR)).isEqualTo(expected)

    verify { anotherTournamentInfoRepository wasNot called }
  }

  @Test
  fun `calculate points for 1000 points tournament with anotherTournamentInfoRepository`() {

    val participants = setOf("PlayerId1", "PlayerId2", "PlayerId3", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7")
    val winners = mapOf(F to setOf("PlayerId4"),
                        SF to setOf("PlayerId1", "PlayerId4"),
                        QF to setOf("PlayerId1", "PlayerId2", "PlayerId4"),
                        R3 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5"),
                        R2 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId7"),
                        R1 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7"))

    val tournaments = listOf(Tournament(id = AN_ID,
                                        tennisTvId = A_TOURNAMENT_ID,
                                        points = 1000))
    val tournamentInfo = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                                participants = participants,
                                                winners = winners)

    val expected = setOf(AtpPlayer(id = "PlayerId1",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 28.0))),
                         AtpPlayer(id = "PlayerId2",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 16.0))),
                         AtpPlayer(id = "PlayerId3",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 1.0))),
                         AtpPlayer(id = "PlayerId4",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 40.0))),
                         AtpPlayer(id = "PlayerId5",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 8.0))),
                         AtpPlayer(id = "PlayerId6",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 2.0))),
                         AtpPlayer(id = "PlayerId7",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 4.0))))

    every { tournamentsRepository.readTournaments() } returns tournaments
    every { tournamentInfoRepository.canProcess(A_TOURNAMENT_ID) } returns false
    every { anotherTournamentInfoRepository.canProcess(A_TOURNAMENT_ID) } returns true
    every { anotherTournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns tournamentInfo

    assertThat(service.calculateFantaPointsFor(AN_ID, A_YEAR)).isEqualTo(expected)

    verify(exactly = 0) { tournamentInfoRepository.retrieveTournamentInfo(any(), any()) }
  }

  @Test
  fun `calculate points for 2000 points tournament`() {

    val participants = setOf("PlayerId1", "PlayerId2", "PlayerId3", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7", "PlayerId8")
    val winners = mapOf(F to setOf("PlayerId4"),
                        SF to setOf("PlayerId1", "PlayerId4"),
                        QF to setOf("PlayerId1", "PlayerId2", "PlayerId4"),
                        R4 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5"),
                        R3 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId8"),
                        R2 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId7", "PlayerId8"),
                        R1 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7", "PlayerId8"))

    val tournaments = listOf(Tournament(id = AN_ID,
                                        tennisTvId = A_TOURNAMENT_ID,
                                        points = 2000))
    val tournamentInfo = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                                participants = participants,
                                                winners = winners)

    val expected = setOf(AtpPlayer(id = "PlayerId1",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 56.0))),
                         AtpPlayer(id = "PlayerId2",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 32.0))),
                         AtpPlayer(id = "PlayerId3",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 0.0))),
                         AtpPlayer(id = "PlayerId4",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 80.0))),
                         AtpPlayer(id = "PlayerId5",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 16.0))),
                         AtpPlayer(id = "PlayerId6",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 2.0))),
                         AtpPlayer(id = "PlayerId7",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 4.0))),
                         AtpPlayer(id = "PlayerId8",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 8.0))))

    every { tournamentsRepository.readTournaments() } returns tournaments
    every { tournamentInfoRepository.canProcess(A_TOURNAMENT_ID) } returns true
    every { tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns tournamentInfo

    assertThat(service.calculateFantaPointsFor(AN_ID, A_YEAR)).isEqualTo(expected)

    verify { anotherTournamentInfoRepository wasNot called }
  }

  @Test
  fun `calculate points for 1000 points tournament with fourth round`() {

    val participants = setOf("PlayerId1", "PlayerId2", "PlayerId3", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7", "PlayerId8")
    val winners = mapOf(F to setOf("PlayerId4"),
                        SF to setOf("PlayerId1", "PlayerId4"),
                        QF to setOf("PlayerId1", "PlayerId2", "PlayerId4"),
                        R4 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5"),
                        R3 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId8"),
                        R2 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId7", "PlayerId8"),
                        R1 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7", "PlayerId8"))

    val tournaments = listOf(Tournament(id = AN_ID,
                                        tennisTvId = A_TOURNAMENT_ID,
                                        points = 1000))
    val tournamentInfo = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                                participants = participants,
                                                winners = winners)

    val expected = setOf(AtpPlayer(id = "PlayerId1",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 28.0))),
                         AtpPlayer(id = "PlayerId2",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 16.0))),
                         AtpPlayer(id = "PlayerId3",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 0.0))),
                         AtpPlayer(id = "PlayerId4",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 40.0))),
                         AtpPlayer(id = "PlayerId5",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 8.0))),
                         AtpPlayer(id = "PlayerId6",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 1.0))),
                         AtpPlayer(id = "PlayerId7",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 2.0))),
                         AtpPlayer(id = "PlayerId8",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 4.0))))

    every { tournamentsRepository.readTournaments() } returns tournaments
    every { tournamentInfoRepository.canProcess(A_TOURNAMENT_ID) } returns true
    every { tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns tournamentInfo

    assertThat(service.calculateFantaPointsFor(AN_ID, A_YEAR)).isEqualTo(expected)

    verify { anotherTournamentInfoRepository wasNot called }
  }

  @Test
  fun `calculate points for 500 points tournament`() {

    val participants = setOf("PlayerId1", "PlayerId2", "PlayerId3", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7")
    val winners = mapOf(F to setOf("PlayerId4"),
                        SF to setOf("PlayerId1", "PlayerId4"),
                        QF to setOf("PlayerId1", "PlayerId2", "PlayerId4"),
                        R3 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5"),
                        R2 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId7"),
                        R1 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7"))

    val tournaments = listOf(Tournament(id = AN_ID,
                                        tennisTvId = A_TOURNAMENT_ID,
                                        points = 500))
    val tournamentInfo = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                                participants = participants,
                                                winners = winners)

    val expected = setOf(AtpPlayer(id = "PlayerId1",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 14.0))),
                         AtpPlayer(id = "PlayerId2",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 8.0))),
                         AtpPlayer(id = "PlayerId3",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 0.0))),
                         AtpPlayer(id = "PlayerId4",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 20.0))),
                         AtpPlayer(id = "PlayerId5",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 4.0))),
                         AtpPlayer(id = "PlayerId6",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 1.0))),
                         AtpPlayer(id = "PlayerId7",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 2.0))))

    every { tournamentsRepository.readTournaments() } returns tournaments
    every { tournamentInfoRepository.canProcess(A_TOURNAMENT_ID) } returns true
    every { tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns tournamentInfo

    assertThat(service.calculateFantaPointsFor(AN_ID, A_YEAR)).isEqualTo(expected)

    verify { anotherTournamentInfoRepository wasNot called }
  }

  @Test
  fun `calculate points for 250 points tournament`() {

    val participants = setOf("PlayerId1", "PlayerId2", "PlayerId3", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7")
    val winners = mapOf(F to setOf("PlayerId4"),
                        SF to setOf("PlayerId1", "PlayerId4"),
                        QF to setOf("PlayerId1", "PlayerId2", "PlayerId4"),
                        R3 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5"),
                        R2 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId7"),
                        R1 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId6", "PlayerId7"))

    val tournaments = listOf(Tournament(id = AN_ID,
                                        tennisTvId = A_TOURNAMENT_ID,
                                        points = 250))
    val tournamentInfo = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                                participants = participants,
                                                winners = winners)

    val expected = setOf(AtpPlayer(id = "PlayerId1",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 7.0))),
                         AtpPlayer(id = "PlayerId2",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 4.0))),
                         AtpPlayer(id = "PlayerId3",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 0.0))),
                         AtpPlayer(id = "PlayerId4",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 10.0))),
                         AtpPlayer(id = "PlayerId5",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 2.0))),
                         AtpPlayer(id = "PlayerId6",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 0.0))),
                         AtpPlayer(id = "PlayerId7",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 1.0))))

    every { tournamentsRepository.readTournaments() } returns tournaments
    every { tournamentInfoRepository.canProcess(A_TOURNAMENT_ID) } returns true
    every { tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns tournamentInfo

    assertThat(service.calculateFantaPointsFor(AN_ID, A_YEAR)).isEqualTo(expected)

    verify { anotherTournamentInfoRepository wasNot called }
  }

  @Test
  fun `calculate points for 250 points tournament without third round`() {

    val participants = setOf("PlayerId1", "PlayerId2", "PlayerId3", "PlayerId4", "PlayerId5", "PlayerId6")
    val winners = mapOf(F to setOf("PlayerId4"),
                        SF to setOf("PlayerId1", "PlayerId4"),
                        QF to setOf("PlayerId1", "PlayerId2", "PlayerId4"),
                        R2 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5"),
                        R1 to setOf("PlayerId1", "PlayerId2", "PlayerId4", "PlayerId5", "PlayerId6"))

    val tournaments = listOf(Tournament(id = AN_ID,
                                        tennisTvId = A_TOURNAMENT_ID,
                                        points = 250))
    val tournamentInfo = CompleteTournamentInfo(tournamentId = A_TOURNAMENT_ID,
                                                participants = participants,
                                                winners = winners)

    val expected = setOf(AtpPlayer(id = "PlayerId1",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 7.0))),
                         AtpPlayer(id = "PlayerId2",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 4.0))),
                         AtpPlayer(id = "PlayerId3",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 0.0))),
                         AtpPlayer(id = "PlayerId4",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 10.0))),
                         AtpPlayer(id = "PlayerId5",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 2.0))),
                         AtpPlayer(id = "PlayerId6",
                                   tournamentPoints = mapOf(A_YEAR to mapOf(AN_ID to 1.0))))

    every { tournamentsRepository.readTournaments() } returns tournaments
    every { tournamentInfoRepository.canProcess(A_TOURNAMENT_ID) } returns true
    every { tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns tournamentInfo

    assertThat(service.calculateFantaPointsFor(AN_ID, A_YEAR)).isEqualTo(expected)

    verify { anotherTournamentInfoRepository wasNot called }
  }

  @Test
  fun `error from repository`() {

    val tournaments = listOf(Tournament(id = AN_ID,
                                        tennisTvId = A_TOURNAMENT_ID,
                                        points = ANY_POINTS))
    val tournamentInfo = ErrorTournamentInfo

    val expected = emptySet<AtpPlayer>()

    every { tournamentsRepository.readTournaments() } returns tournaments
    every { tournamentInfoRepository.canProcess(A_TOURNAMENT_ID) } returns true
    every { tournamentInfoRepository.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR) } returns tournamentInfo

    assertThat(service.calculateFantaPointsFor(AN_ID, A_YEAR)).isEqualTo(expected)

    verify { anotherTournamentInfoRepository wasNot called }
  }

  companion object {

    private const val AN_ID = 1
    private const val A_TOURNAMENT_ID = 123
    private const val A_YEAR = 2222
    private const val ANY_POINTS = 1000
  }
}