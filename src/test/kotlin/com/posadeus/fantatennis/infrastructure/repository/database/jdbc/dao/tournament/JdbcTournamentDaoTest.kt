package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcTournamentDto.aJdbcTournamentDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.Types
import java.time.LocalDate

class JdbcTournamentDaoTest {

  private val jdbcTemplate: JdbcTemplate = mockk()

  private val dao: TournamentDao = JdbcTournamentDao(jdbcTemplate)

  @Test
  fun `error on repository operation`() {

    val expected = emptyList<JdbcTournamentDto>()

    every {
      jdbcTemplate.query(RETRIEVE_TOURNAMENTS_QUERY, arrayOf(A_YEAR), intArrayOf(Types.INTEGER), any<RowMapper<JdbcTournamentDto>>())
    } throws RuntimeException()

    assertThat(dao.retrieveAllBy(A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `no results returned by the query`() {

    val expected = emptyList<JdbcTournamentDto>()

    every {
      jdbcTemplate.query(RETRIEVE_TOURNAMENTS_QUERY, arrayOf(A_YEAR), intArrayOf(Types.INTEGER), any<RowMapper<JdbcTournamentDto>>())
    } returns emptyList()

    assertThat(dao.retrieveAllBy(A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `retrieve results successfully`() {

    val jdbcTournament1 = aJdbcTournamentDto(tournamentId = AN_ID,
                                             atpTourId = AN_ATP_TOUR_ID,
                                             tennisTvId = A_TENNIS_TV_ID,
                                             name = A_NAME,
                                             points = A_POINTS,
                                             location = A_LOCATION,
                                             surface = A_SURFACE,
                                             year = A_YEAR,
                                             startDate = A_START_DATE,
                                             endDate = AN_END_DATE)
    val jdbcTournament2 = aJdbcTournamentDto(tournamentId = ANOTHER_ID,
                                             atpTourId = ANOTHER_ATP_TOUR_ID,
                                             tennisTvId = ANOTHER_TENNIS_TV_ID,
                                             name = ANOTHER_NAME,
                                             points = ANOTHER_POINTS,
                                             location = ANOTHER_LOCATION,
                                             surface = ANOTHER_SURFACE,
                                             year = A_YEAR,
                                             startDate = ANOTHER_START_DATE,
                                             endDate = ANOTHER_END_DATE)
    val expected = listOf(jdbcTournament1, jdbcTournament2)

    every {
      jdbcTemplate.query(RETRIEVE_TOURNAMENTS_QUERY, arrayOf(A_YEAR), intArrayOf(Types.INTEGER), any<RowMapper<JdbcTournamentDto>>())
    } returns expected

    assertThat(dao.retrieveAllBy(A_YEAR)).isEqualTo(expected)
  }

  companion object {

    private const val AN_ID = 1
    private const val ANOTHER_ID = 2
    private const val AN_ATP_TOUR_ID = 223
    private const val ANOTHER_ATP_TOUR_ID = 224
    private const val A_TENNIS_TV_ID = 12
    private const val ANOTHER_TENNIS_TV_ID = 34
    private const val A_POINTS = 1000
    private const val ANOTHER_POINTS = 500
    private const val A_YEAR = 2025
    private const val A_NAME = "A_NAME"
    private const val ANOTHER_NAME = "ANOTHER_NAME"
    private const val A_LOCATION = "A_LOCATION"
    private const val ANOTHER_LOCATION = "ANOTHER_LOCATION"
    private const val A_SURFACE = "A_SURFACE"
    private const val ANOTHER_SURFACE = "ANOTHER_SURFACE"

    private val A_START_DATE = LocalDate.of(2025, 1, 1)
    private val AN_END_DATE = LocalDate.of(2025, 1, 7)
    private val ANOTHER_START_DATE = LocalDate.of(2025, 1, 8)
    private val ANOTHER_END_DATE = LocalDate.of(2025, 1, 14)

    private val RETRIEVE_TOURNAMENTS_QUERY = """
      SELECT *
      FROM TOURNAMENTS
      WHERE `YEAR` = ?;
    """.trimIndent()
  }
}