package com.posadeus.fantatennis.infrastructure.repository.tennistv

import com.posadeus.fantatennis.domain.infrastructure.TournamentsRegistryRepository
import com.posadeus.fantatennis.domain.model.Surface
import com.posadeus.fantatennis.domain.model.TournamentRegistry
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.FoundTournamentsRegistry
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.NotFoundTournamentsRegistry
import com.posadeus.fantatennis.infrastructure.client.tennistv.TennisTvClient
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.TennisTvTournamentRegistry
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.TennisTvTournamentsRegistryResponse
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TennisTvTournamentsRegistryRepositoryTest {

  private val tennisTvClient: TennisTvClient = mockk()

  private val repository: TournamentsRegistryRepository = TennisTvTournamentsRegistryRepository(tennisTvClient)

  @Test
  fun `retrieve tournament registry`() {

    val tournamentRegistryResponse1 = TennisTvTournamentRegistry(id = 23456,
                                                                 year = A_YEAR,
                                                                 name = A_NAME,
                                                                 start = A_START_DATE,
                                                                 end = AN_END_DATE,
                                                                 type = "250",
                                                                 location = A_LOCATION,
                                                                 surface = "Hard")
    val tournamentRegistryResponse2 = TennisTvTournamentRegistry(id = 76543,
                                                                 year = A_YEAR,
                                                                 name = ANOTHER_NAME,
                                                                 start = ANOTHER_START_DATE,
                                                                 end = ANOTHER_END_DATE,
                                                                 type = "GS",
                                                                 location = ANOTHER_LOCATION,
                                                                 surface = "GRASS")
    val tournamentRegistryResponse3 = TennisTvTournamentRegistry(id = A_TENNIS_TV_ID,
                                                                 year = A_YEAR,
                                                                 name = A_THIRD_NAME,
                                                                 start = ANOTHER_START_DATE,
                                                                 end = ANOTHER_END_DATE,
                                                                 type = "UC",
                                                                 location = ANOTHER_LOCATION,
                                                                 surface = A_SURFACE)
    val response = TennisTvTournamentsRegistryResponse(listOf(tournamentRegistryResponse1,
                                                              tournamentRegistryResponse2,
                                                              tournamentRegistryResponse3))

    val tournament1 = TournamentRegistry(atpTourId = 23456,
                                         tennisTvId = 23456,
                                         name = A_NAME,
                                         startDate = A_START_DATE,
                                         endDate = AN_END_DATE,
                                         year = A_YEAR,
                                         points = 250,
                                         surface = Surface.HARD,
                                         location = A_LOCATION)
    val tournament2 = TournamentRegistry(atpTourId = 76543,
                                         tennisTvId = 76543,
                                         name = ANOTHER_NAME,
                                         startDate = ANOTHER_START_DATE,
                                         endDate = ANOTHER_END_DATE,
                                         year = A_YEAR,
                                         points = 2000,
                                         surface = Surface.GRASS,
                                         location = ANOTHER_LOCATION)
    val expected = FoundTournamentsRegistry(listOf(tournament1, tournament2))

    every { tennisTvClient.retrieveTournamentsRegistry(A_YEAR) } returns response

    assertThat(repository.retrieveAllTournamentsFor(A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `unexpected surface`() {

    val tournamentRegistryResponse1 = TennisTvTournamentRegistry(id = A_TENNIS_TV_ID,
                                                                 year = A_YEAR,
                                                                 name = A_NAME,
                                                                 start = A_START_DATE,
                                                                 end = AN_END_DATE,
                                                                 type = A_TYPE,
                                                                 location = A_LOCATION,
                                                                 surface = "AN_INVALID_SURFACE")
    val response = TennisTvTournamentsRegistryResponse(listOf(tournamentRegistryResponse1))

    every { tennisTvClient.retrieveTournamentsRegistry(A_YEAR) } returns response

    assertThrows<IllegalArgumentException> { repository.retrieveAllTournamentsFor(A_YEAR) }
  }

  @Test
  fun `no results for the year`() {

    val response = TennisTvTournamentsRegistryResponse(emptyList())

    val expected = NotFoundTournamentsRegistry

    every { tennisTvClient.retrieveTournamentsRegistry(A_YEAR) } returns response

    assertThat(repository.retrieveAllTournamentsFor(A_YEAR)).isEqualTo(expected)
  }

  companion object {

    private const val A_YEAR = 2000
    private const val A_TENNIS_TV_ID = 345
    private const val A_NAME = "A_NAME"
    private const val ANOTHER_NAME = "ANOTHER_NAME"
    private const val A_THIRD_NAME = "A_THIRD_NAME"
    private const val A_LOCATION = "A_LOCATION"
    private const val ANOTHER_LOCATION = "ANOTHER_LOCATION"
    private const val A_START_DATE = "2025-01-01"
    private const val AN_END_DATE = "2025-01-02"
    private const val ANOTHER_START_DATE = "2025-01-03"
    private const val ANOTHER_END_DATE = "2025-01-04"
    private const val A_SURFACE = "A_SURFACE"
    private const val A_TYPE = "250"
  }
}