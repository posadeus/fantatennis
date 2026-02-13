package com.posadeus.fantatennis.infrastructure.repository.database

import com.posadeus.fantatennis.domain.exception.InvalidTournamentException
import com.posadeus.fantatennis.domain.infrastructure.PersistTournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestNewJdbcTournamentDto
import io.mockk.*
import org.assertj.core.api.AssertionsForClassTypes
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SqlPersistTournamentsRepositoryTest {

  private val tournamentDao: TournamentDao = mockk()

  private val repository: PersistTournamentsRepository = SqlPersistTournamentsRepository(tournamentDao)

  @Test
  fun `error from dao`() {

    val tournament1 = TournamentRegistry(atpTourId = AN_ATP_TOUR_ID,
                                         tennisTvId = A_TENNIS_TV_ID,
                                         name = A_NAME,
                                         startDate = A_START_DATE,
                                         endDate = AN_END_DATE,
                                         year = A_YEAR,
                                         points = A_POINTS,
                                         surface = A_SURFACE_ENUM,
                                         location = A_LOCATION)
    val tournament2 = TournamentRegistry(atpTourId = ANOTHER_ATP_TOUR_ID,
                                         tennisTvId = ANOTHER_TENNIS_TV_ID,
                                         name = ANOTHER_NAME,
                                         startDate = ANOTHER_START_DATE,
                                         endDate = ANOTHER_END_DATE,
                                         year = A_YEAR,
                                         points = ANOTHER_POINTS,
                                         surface = ANOTHER_SURFACE_ENUM,
                                         location = ANOTHER_LOCATION)
    val tournaments = TournamentsRegistry.FoundTournamentsRegistry(listOf(tournament1, tournament2))

    val tournamentDao1 = TestNewJdbcTournamentDto.aNewJdbcTournamentDto(atpTourId = AN_ATP_TOUR_ID,
                                                                        tennisTvId = A_TENNIS_TV_ID,
                                                                        name = A_NAME,
                                                                        startDate = A_START_DATE_LOCAL_DATE,
                                                                        endDate = AN_END_DATE_LOCAL_DATE,
                                                                        year = A_YEAR,
                                                                        points = A_POINTS,
                                                                        surface = A_SURFACE,
                                                                        location = A_LOCATION)
    val tournamentDao2 = TestNewJdbcTournamentDto.aNewJdbcTournamentDto(atpTourId = ANOTHER_ATP_TOUR_ID,
                                                                        tennisTvId = ANOTHER_TENNIS_TV_ID,
                                                                        name = ANOTHER_NAME,
                                                                        startDate = ANOTHER_START_DATE_LOCAL_DATE,
                                                                        endDate = ANOTHER_END_DATE_LOCAL_DATE,
                                                                        year = A_YEAR,
                                                                        points = ANOTHER_POINTS,
                                                                        surface = ANOTHER_SURFACE,
                                                                        location = ANOTHER_LOCATION)
    val tournamentsDao = listOf(tournamentDao1, tournamentDao2)

    val errorMessage = "It's Friday, bro!"

    val expected = TournamentsCreated.ErrorTournamentsCreation(errorMessage)

    every { tournamentDao.persistNewTournaments(tournamentsDao) } throws InvalidTournamentException(errorMessage)

    AssertionsForClassTypes.assertThat(repository.persistNewTournaments(tournaments)).isEqualTo(expected)

    verify(exactly = 1) { tournamentDao.persistNewTournaments(tournamentsDao) }
  }

  @Test
  fun `persist all the tournaments`() {

    val tournament1 = TournamentRegistry(atpTourId = AN_ATP_TOUR_ID,
                                         tennisTvId = A_TENNIS_TV_ID,
                                         name = A_NAME,
                                         startDate = A_START_DATE,
                                         endDate = AN_END_DATE,
                                         year = A_YEAR,
                                         points = A_POINTS,
                                         surface = A_SURFACE_ENUM,
                                         location = A_LOCATION)
    val tournament2 = TournamentRegistry(atpTourId = ANOTHER_ATP_TOUR_ID,
                                         tennisTvId = ANOTHER_TENNIS_TV_ID,
                                         name = ANOTHER_NAME,
                                         startDate = ANOTHER_START_DATE,
                                         endDate = ANOTHER_END_DATE,
                                         year = A_YEAR,
                                         points = ANOTHER_POINTS,
                                         surface = ANOTHER_SURFACE_ENUM,
                                         location = ANOTHER_LOCATION)
    val tournaments = TournamentsRegistry.FoundTournamentsRegistry(listOf(tournament1, tournament2))

    val tournamentDao1 = TestNewJdbcTournamentDto.aNewJdbcTournamentDto(atpTourId = AN_ATP_TOUR_ID,
                                                                        tennisTvId = A_TENNIS_TV_ID,
                                                                        name = A_NAME,
                                                                        startDate = A_START_DATE_LOCAL_DATE,
                                                                        endDate = AN_END_DATE_LOCAL_DATE,
                                                                        year = A_YEAR,
                                                                        points = A_POINTS,
                                                                        surface = A_SURFACE,
                                                                        location = A_LOCATION)
    val tournamentDao2 = TestNewJdbcTournamentDto.aNewJdbcTournamentDto(atpTourId = ANOTHER_ATP_TOUR_ID,
                                                                        tennisTvId = ANOTHER_TENNIS_TV_ID,
                                                                        name = ANOTHER_NAME,
                                                                        startDate = ANOTHER_START_DATE_LOCAL_DATE,
                                                                        endDate = ANOTHER_END_DATE_LOCAL_DATE,
                                                                        year = A_YEAR,
                                                                        points = ANOTHER_POINTS,
                                                                        surface = ANOTHER_SURFACE,
                                                                        location = ANOTHER_LOCATION)
    val tournamentsDao = listOf(tournamentDao1, tournamentDao2)

    val expected = TournamentsCreated.SuccessTournamentsCreated

    every { tournamentDao.persistNewTournaments(tournamentsDao) } just runs

    AssertionsForClassTypes.assertThat(repository.persistNewTournaments(tournaments)).isEqualTo(expected)

    verify(exactly = 1) { tournamentDao.persistNewTournaments(tournamentsDao) }
  }

  companion object {

    private const val AN_ATP_TOUR_ID = 12345
    private const val ANOTHER_ATP_TOUR_ID = 98765
    private const val A_TENNIS_TV_ID = 23456
    private const val ANOTHER_TENNIS_TV_ID = 76543
    private const val A_YEAR = 1234
    private const val A_NAME = "A_NAME"
    private const val ANOTHER_NAME = "ANOTHER_NAME"
    private const val A_POINTS = 250
    private const val ANOTHER_POINTS = 500
    private const val A_LOCATION = "A_LOCATION"
    private const val ANOTHER_LOCATION = "ANOTHER_LOCATION"
    private const val A_START_DATE = "2025-01-01"
    private const val AN_END_DATE = "2025-01-02"
    private const val ANOTHER_START_DATE = "2025-01-03"
    private const val ANOTHER_END_DATE = "2025-01-04"
    private const val A_SURFACE = "HARD"
    private const val ANOTHER_SURFACE = "CLAY"

    private val A_SURFACE_ENUM = Surface.HARD
    private val ANOTHER_SURFACE_ENUM = Surface.CLAY
    private val A_START_DATE_LOCAL_DATE = LocalDate.of(2025, 1, 1)
    private val AN_END_DATE_LOCAL_DATE = LocalDate.of(2025, 1, 2)
    private val ANOTHER_START_DATE_LOCAL_DATE = LocalDate.of(2025, 1, 3)
    private val ANOTHER_END_DATE_LOCAL_DATE = LocalDate.of(2025, 1, 4)
  }
}