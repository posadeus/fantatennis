package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.TournamentsRepository
import com.posadeus.fantatennis.domain.model.Tournament
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TournamentsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.TournamentsEntity
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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
    private const val A_SURFACE = "HARD"
    private const val ANOTHER_SURFACE = "CLAY"
  }
}