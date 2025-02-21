package com.posadeus.fantatennis.domain.service.tournament

import com.posadeus.fantatennis.domain.infrastructure.TournamentRegistryRepository
import com.posadeus.fantatennis.domain.infrastructure.TournamentsRepository
import com.posadeus.fantatennis.domain.model.Surface
import com.posadeus.fantatennis.domain.model.TournamentRegistry
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.FoundTournamentsRegistry
import io.mockk.*
import org.junit.jupiter.api.Test

class AddTournamentsServiceTest {

  private val tournamentRegistryRepository: TournamentRegistryRepository = mockk()
  private val tournamentsRepository: TournamentsRepository = mockk()

  private val service = AddTournamentsService(tournamentRegistryRepository, tournamentsRepository)

  @Test
  fun `add tournaments successfully`() {

    val tournament1 = TournamentRegistry(atpId = AN_ATP_ID,
                                         tennisTvId = A_TENNIS_TV_ID,
                                         name = A_NAME,
                                         startDate = A_START_DATE,
                                         endDate = AN_END_DATE,
                                         year = A_YEAR,
                                         points = A_POINTS,
                                         surface = A_SURFACE,
                                         location = A_LOCATION)
    val tournament2 = TournamentRegistry(atpId = ANOTHER_ATP_ID,
                                         tennisTvId = ANOTHER_TENNIS_TV_ID,
                                         name = ANOTHER_NAME,
                                         startDate = ANOTHER_START_DATE,
                                         endDate = ANOTHER_END_DATE,
                                         year = A_YEAR,
                                         points = ANOTHER_POINTS,
                                         surface = ANOTHER_SURFACE,
                                         location = ANOTHER_LOCATION)
    val tournaments = FoundTournamentsRegistry(listOf(tournament1, tournament2))

    every { tournamentRegistryRepository.retrieveAllTournamentsFor(A_YEAR) } returns tournaments
    every { tournamentsRepository.persist(tournaments) } just runs

    service.addTournamentsFor(A_YEAR)

    verify(exactly = 1) { tournamentRegistryRepository.retrieveAllTournamentsFor(A_YEAR) }
    verify(exactly = 1) { tournamentsRepository.persist(tournaments) }
  }

  companion object {

    private const val A_YEAR = 2000
    private const val AN_ATP_ID = 123
    private const val A_TENNIS_TV_ID = 123
    private const val A_NAME = "A_NAME"
    private const val A_START_DATE = "2025-01-01"
    private const val AN_END_DATE = "2025-01-07"
    private const val A_POINTS = 2000
    private const val A_LOCATION = "A_LOCATION"
    private const val ANOTHER_ATP_ID = 456
    private const val ANOTHER_TENNIS_TV_ID = 456
    private const val ANOTHER_NAME = "ANOTHER_NAME"
    private const val ANOTHER_START_DATE = "2025-01-08"
    private const val ANOTHER_END_DATE = "2025-01-14"
    private const val ANOTHER_POINTS = 500
    private const val ANOTHER_LOCATION = "ANOTHER_LOCATION"

    private val A_SURFACE = Surface.CLAY
    private val ANOTHER_SURFACE = Surface.HARD
  }
}