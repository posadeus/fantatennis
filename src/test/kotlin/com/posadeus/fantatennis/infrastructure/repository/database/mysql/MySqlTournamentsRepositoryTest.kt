package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.TournamentsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.FoundTournamentsRegistry
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TournamentsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.TournamentsEntity
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MySqlTournamentsRepositoryTest {

  private val dao: TournamentsDao = mockk()

  private val repository: TournamentsRepository = MySqlTournamentsRepository(dao)

  @Test
  fun `read all tournaments by year`() {

    val tournamentsEntities = listOf(TournamentsEntity(id = AN_ID,
                                                       atpTourId = AN_ATP_TOUR_ID,
                                                       tennisTvId = A_TENNIS_TV_ID,
                                                       name = A_NAME,
                                                       points = A_POINTS,
                                                       location = A_LOCATION,
                                                       surface = A_SURFACE,
                                                       year = A_YEAR),
                                     TournamentsEntity(id = ANOTHER_ID,
                                                       atpTourId = ANOTHER_ATP_TOUR_ID,
                                                       tennisTvId = ANOTHER_TENNIS_TV_ID,
                                                       name = ANOTHER_NAME,
                                                       points = ANOTHER_POINTS,
                                                       location = ANOTHER_LOCATION,
                                                       surface = ANOTHER_SURFACE,
                                                       year = A_YEAR))

    val expected = listOf(Tournament(id = AN_ID,
                                     tennisTvId = A_TENNIS_TV_ID,
                                     points = A_POINTS,
                                     year = A_YEAR),
                          Tournament(id = ANOTHER_ID,
                                     tennisTvId = ANOTHER_TENNIS_TV_ID,
                                     points = ANOTHER_POINTS,
                                     year = A_YEAR))

    every { dao.findByYear(A_YEAR) } returns tournamentsEntities

    assertThat(repository.readTournaments(A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `read all tournaments`() {

    val tournamentsEntities = listOf(TournamentsEntity(id = AN_ID,
                                                       atpTourId = AN_ATP_TOUR_ID,
                                                       tennisTvId = A_TENNIS_TV_ID,
                                                       name = A_NAME,
                                                       points = A_POINTS,
                                                       location = A_LOCATION,
                                                       surface = A_SURFACE,
                                                       year = A_YEAR),
                                     TournamentsEntity(id = ANOTHER_ID,
                                                       atpTourId = ANOTHER_ATP_TOUR_ID,
                                                       tennisTvId = ANOTHER_TENNIS_TV_ID,
                                                       name = ANOTHER_NAME,
                                                       points = ANOTHER_POINTS,
                                                       location = ANOTHER_LOCATION,
                                                       surface = ANOTHER_SURFACE,
                                                       year = ANOTHER_YEAR))

    val expected = listOf(Tournament(id = AN_ID,
                                     tennisTvId = A_TENNIS_TV_ID,
                                     points = A_POINTS,
                                     year = A_YEAR),
                          Tournament(id = ANOTHER_ID,
                                     tennisTvId = ANOTHER_TENNIS_TV_ID,
                                     points = ANOTHER_POINTS,
                                     year = ANOTHER_YEAR))

    every { dao.findAll() } returns tournamentsEntities

    assertThat(repository.getAllTournaments()).isEqualTo(expected)
  }

  @Test
  fun persist() {

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
    val tournamentsRegistry = FoundTournamentsRegistry(listOf(tournament1, tournament2))

    val tournamentsEntity1 = TournamentsEntity(atpTourId = AN_ATP_TOUR_ID,
                                               tennisTvId = A_TENNIS_TV_ID,
                                               name = A_NAME,
                                               points = A_POINTS,
                                               location = A_LOCATION,
                                               surface = A_SURFACE,
                                               year = A_YEAR,
                                               startDate = A_START_DATE,
                                               endDate = AN_END_DATE)
    val tournamentsEntity2 = TournamentsEntity(atpTourId = ANOTHER_ATP_TOUR_ID,
                                               tennisTvId = ANOTHER_TENNIS_TV_ID,
                                               name = ANOTHER_NAME,
                                               points = ANOTHER_POINTS,
                                               location = ANOTHER_LOCATION,
                                               surface = ANOTHER_SURFACE,
                                               year = A_YEAR,
                                               startDate = ANOTHER_START_DATE,
                                               endDate = ANOTHER_END_DATE)
    val entities = listOf(tournamentsEntity1, tournamentsEntity2)

    val tournamentsEntityPersisted1 = tournamentsEntity1.copy(id = 1)
    val tournamentsEntityPersisted2 = tournamentsEntity2.copy(id = 2)
    val persistedEntities = listOf(tournamentsEntityPersisted1, tournamentsEntityPersisted2)

    every { dao.saveAll(entities) } returns persistedEntities

    repository.persist(tournamentsRegistry)
  }

  @Test
  fun `persist fails`() {

    every { dao.saveAll(ANY_TOURNAMENTS_ENTITIES) } throws Exception()

    assertThrows<Exception> { repository.persist(ANY_FOUND_TOURNAMENTS_REGISTRY) }
  }

  companion object {

    private const val AN_ID = 1
    private const val ANOTHER_ID = 2
    private const val AN_ATP_TOUR_ID = 12345
    private const val ANOTHER_ATP_TOUR_ID = 98765
    private const val A_TENNIS_TV_ID = 23456
    private const val ANOTHER_TENNIS_TV_ID = 76543
    private const val A_YEAR = 1234
    private const val ANOTHER_YEAR = 2222
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
    private val ANY_FOUND_TOURNAMENTS_REGISTRY = FoundTournamentsRegistry(emptyList())
    private val ANY_TOURNAMENTS_ENTITIES = emptyList<TournamentsEntity>()
  }
}