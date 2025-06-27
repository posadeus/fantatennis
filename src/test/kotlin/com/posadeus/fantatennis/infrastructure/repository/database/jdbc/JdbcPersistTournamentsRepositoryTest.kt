package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.exception.InvalidTournamentException
import com.posadeus.fantatennis.domain.infrastructure.PersistTournamentsRepository
import com.posadeus.fantatennis.domain.model.Surface
import com.posadeus.fantatennis.domain.model.TournamentRegistry
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.FoundTournamentsRegistry
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcPersistTournamentsRepositoryTest {

  private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val repository: PersistTournamentsRepository = JdbcPersistTournamentsRepository(namedParameterJdbcTemplate)

  @Test
  fun `not all tournaments are persisted`() {

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
    val tournaments = FoundTournamentsRegistry(listOf(tournament1, tournament2))

    val batchElement1 = mapOf("atpTourId" to AN_ATP_TOUR_ID,
                              "tennisTvId" to A_TENNIS_TV_ID,
                              "name" to A_NAME,
                              "points" to A_POINTS,
                              "location" to A_LOCATION,
                              "surface" to A_SURFACE,
                              "year" to A_YEAR,
                              "startDate" to A_START_DATE,
                              "endDate" to AN_END_DATE)
    val batchElement2 = mapOf("atpTourId" to ANOTHER_ATP_TOUR_ID,
                              "tennisTvId" to ANOTHER_TENNIS_TV_ID,
                              "name" to ANOTHER_NAME,
                              "points" to ANOTHER_POINTS,
                              "location" to ANOTHER_LOCATION,
                              "surface" to ANOTHER_SURFACE,
                              "year" to A_YEAR,
                              "startDate" to ANOTHER_START_DATE,
                              "endDate" to ANOTHER_END_DATE)
    val paramSource = arrayOf(batchElement1, batchElement2)

    val expectedMessage = "Unexpected error during insert: Tournaments [98765] not inserted, operation reverted."

    every { namedParameterJdbcTemplate.batchUpdate(INSERT_TOURNAMENTS_QUERY, paramSource) } returns intArrayOf(1, 0)

    assertThrowsWithMessage<InvalidTournamentException>(expectedMessage) { repository.persistAll(tournaments) }

    verify(exactly = 1) { namedParameterJdbcTemplate.batchUpdate(INSERT_TOURNAMENTS_QUERY, paramSource) }
  }

  @Test
  fun `multiple tournaments are not persisted`() {

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
    val tournaments = FoundTournamentsRegistry(listOf(tournament1, tournament2))

    val batchElement1 = mapOf("atpTourId" to AN_ATP_TOUR_ID,
                              "tennisTvId" to A_TENNIS_TV_ID,
                              "name" to A_NAME,
                              "points" to A_POINTS,
                              "location" to A_LOCATION,
                              "surface" to A_SURFACE,
                              "year" to A_YEAR,
                              "startDate" to A_START_DATE,
                              "endDate" to AN_END_DATE)
    val batchElement2 = mapOf("atpTourId" to ANOTHER_ATP_TOUR_ID,
                              "tennisTvId" to ANOTHER_TENNIS_TV_ID,
                              "name" to ANOTHER_NAME,
                              "points" to ANOTHER_POINTS,
                              "location" to ANOTHER_LOCATION,
                              "surface" to ANOTHER_SURFACE,
                              "year" to A_YEAR,
                              "startDate" to ANOTHER_START_DATE,
                              "endDate" to ANOTHER_END_DATE)
    val paramSource = arrayOf(batchElement1, batchElement2)

    val expectedMessage = "Unexpected error during insert: Tournaments [12345, 98765] not inserted, operation reverted."

    every { namedParameterJdbcTemplate.batchUpdate(INSERT_TOURNAMENTS_QUERY, paramSource) } returns intArrayOf(0, 0)

    assertThrowsWithMessage<InvalidTournamentException>(expectedMessage) { repository.persistAll(tournaments) }

    verify(exactly = 1) { namedParameterJdbcTemplate.batchUpdate(INSERT_TOURNAMENTS_QUERY, paramSource) }
  }

  @Test
  fun `exception during operation`() {

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
    val tournaments = FoundTournamentsRegistry(listOf(tournament1, tournament2))

    val batchElement1 = mapOf("atpTourId" to AN_ATP_TOUR_ID,
                              "tennisTvId" to A_TENNIS_TV_ID,
                              "name" to A_NAME,
                              "points" to A_POINTS,
                              "location" to A_LOCATION,
                              "surface" to A_SURFACE,
                              "year" to A_YEAR,
                              "startDate" to A_START_DATE,
                              "endDate" to AN_END_DATE)
    val batchElement2 = mapOf("atpTourId" to ANOTHER_ATP_TOUR_ID,
                              "tennisTvId" to ANOTHER_TENNIS_TV_ID,
                              "name" to ANOTHER_NAME,
                              "points" to ANOTHER_POINTS,
                              "location" to ANOTHER_LOCATION,
                              "surface" to ANOTHER_SURFACE,
                              "year" to A_YEAR,
                              "startDate" to ANOTHER_START_DATE,
                              "endDate" to ANOTHER_END_DATE)
    val paramSource = arrayOf(batchElement1, batchElement2)

    val expectedMessage = "Unexpected error during insert: I'm an error."

    every { namedParameterJdbcTemplate.batchUpdate(INSERT_TOURNAMENTS_QUERY, paramSource) } throws RuntimeException("I'm an error.")

    assertThrowsWithMessage<InvalidTournamentException>(expectedMessage) { repository.persistAll(tournaments) }

    verify(exactly = 1) { namedParameterJdbcTemplate.batchUpdate(INSERT_TOURNAMENTS_QUERY, paramSource) }
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
    val tournaments = FoundTournamentsRegistry(listOf(tournament1, tournament2))

    val batchElement1 = mapOf("atpTourId" to AN_ATP_TOUR_ID,
                              "tennisTvId" to A_TENNIS_TV_ID,
                              "name" to A_NAME,
                              "points" to A_POINTS,
                              "location" to A_LOCATION,
                              "surface" to A_SURFACE,
                              "year" to A_YEAR,
                              "startDate" to A_START_DATE,
                              "endDate" to AN_END_DATE)
    val batchElement2 = mapOf("atpTourId" to ANOTHER_ATP_TOUR_ID,
                              "tennisTvId" to ANOTHER_TENNIS_TV_ID,
                              "name" to ANOTHER_NAME,
                              "points" to ANOTHER_POINTS,
                              "location" to ANOTHER_LOCATION,
                              "surface" to ANOTHER_SURFACE,
                              "year" to A_YEAR,
                              "startDate" to ANOTHER_START_DATE,
                              "endDate" to ANOTHER_END_DATE)
    val paramSource = arrayOf(batchElement1, batchElement2)

    every { namedParameterJdbcTemplate.batchUpdate(INSERT_TOURNAMENTS_QUERY, paramSource) } returns intArrayOf(1, 1)

    repository.persistAll(tournaments)

    verify(exactly = 1) { namedParameterJdbcTemplate.batchUpdate(INSERT_TOURNAMENTS_QUERY, paramSource) }
  }

  private inline fun <reified T : Throwable> assertThrowsWithMessage(expectedMessage: String, block: () -> Unit) {

    val exception = assertThrows<T> { block() }

    assertThat(exception.message).isEqualTo(expectedMessage)
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

    private val INSERT_TOURNAMENTS_QUERY = """
      INSERT INTO TOURNAMENTS
      (ATP_TOUR_ID, TENNIS_TV_ID, NAME, POINTS, LOCATION, SURFACE, `YEAR`, START_DATE, END_DATE)
      VALUES(:atpTourId, :tennisTvId, :name, :points, :location, :surface, :year, :startDate, :endDate);
    """.trimIndent()
  }
}