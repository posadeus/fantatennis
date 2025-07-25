package com.posadeus.fantatennis.infrastructure.repository.database.mysql.it

import com.posadeus.fantatennis.domain.model.Tournament
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TournamentsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.TournamentsEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

// FIXME Fix all the IT and use a H2 DB creating the tables for test manually
@ExtendWith(SpringExtension::class)
@DataJpaTest
@ContextConfiguration(classes = [com.posadeus.fantatennis.app.Application::class])
@ComponentScan(basePackages = ["com.posadeus.fantatennis.app.configuration.infrastructure.mysql"])
class MySqlTournamentsRepositoryIT {

  @Autowired
  private lateinit var tournamentsDao: TournamentsDao

  @Autowired
  private lateinit var mySqlTournamentsRepository: MySqlTournamentsRepository

  @BeforeEach
  fun setUp() {

    deleteAll()
  }

  @Test
  fun `read tournaments`() {

    assertThat(tournamentsDao.findAll()).isEqualTo(arrayListOf<TournamentsEntity>())

    val entity1 = TournamentsEntity(id = AN_ID,
                                    atpTourId = AN_ATP_TOUR_ID,
                                    tennisTvId = A_TENNIS_TV_ID,
                                    name = A_NAME,
                                    points = A_POINTS,
                                    location = A_LOCATION,
                                    surface = A_SURFACE,
                                    year = A_YEAR)
    val entity2 = TournamentsEntity(id = ANOTHER_ID,
                                    atpTourId = ANOTHER_ATP_TOUR_ID,
                                    tennisTvId = ANOTHER_TENNIS_TV_ID,
                                    name = ANOTHER_NAME,
                                    points = ANOTHER_POINTS,
                                    location = ANOTHER_LOCATION,
                                    surface = ANOTHER_SURFACE,
                                    year = ANOTHER_YEAR)
    val entities = arrayListOf(entity1, entity2)

    tournamentsDao.saveAll(entities)

    val expected = listOf(Tournament(id = AN_ID,
                                     tennisTvId = A_TENNIS_TV_ID,
                                     points = A_POINTS,
                                     year = A_YEAR),
                          Tournament(id = ANOTHER_ID,
                                     tennisTvId = ANOTHER_TENNIS_TV_ID,
                                     points = ANOTHER_POINTS,
                                     year = ANOTHER_YEAR))

    assertThat(mySqlTournamentsRepository.getAllTournaments()).isEqualTo(expected)
  }

  private fun deleteAll() {

    tournamentsDao.deleteAll()
  }

  companion object {

    private const val AN_ID = 1
    private const val ANOTHER_ID = 2
    private const val AN_ATP_TOUR_ID = 12345
    private const val ANOTHER_ATP_TOUR_ID = 98765
    private const val A_TENNIS_TV_ID = 23456
    private const val ANOTHER_TENNIS_TV_ID = 76543
    private const val A_YEAR = 1234
    private const val ANOTHER_YEAR = 2344
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