package com.posadeus.fantatennis.domain.service.tournament

import com.posadeus.fantatennis.domain.infrastructure.TournamentsRepository
import com.posadeus.fantatennis.domain.model.Tournament
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RetrieveTournamentsServiceTest {

  private val repository: TournamentsRepository = mockk()

  private val service = RetrieveTournamentsService(repository)

  @Test
  fun `retrieve tournaments`() {

    val expected = listOf(Tournament(id = AN_ID,
                                     tennisTvId = A_TENNIS_TV_ID,
                                     points = A_POINTS,
                                     year = A_YEAR),
                          Tournament(id = ANOTHER_ID,
                                     tennisTvId = ANOTHER_TENNIS_TV_ID,
                                     points = ANOTHER_POINTS,
                                     year = ANOTHER_YEAR))

    every { repository.getAllTournaments() } returns expected

    assertThat(service.retrieveAll()).isEqualTo(expected)
  }

  companion object {

    private const val AN_ID = 1234
    private const val A_TENNIS_TV_ID = 4321
    private const val A_POINTS = 1000
    private const val A_YEAR = 2025
    private const val ANOTHER_ID = 5678
    private const val ANOTHER_TENNIS_TV_ID = 8765
    private const val ANOTHER_POINTS = 250
    private const val ANOTHER_YEAR = 2024
  }
}