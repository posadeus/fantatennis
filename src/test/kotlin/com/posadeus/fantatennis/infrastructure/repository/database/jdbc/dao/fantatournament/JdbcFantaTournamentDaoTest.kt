package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournament

import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto.Companion.fantaTournamentRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcFantaTournamentDto.aJdbcFantaTournamentDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcFantaTournamentDaoTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val dao: FantaTournamentDao = JdbcFantaTournamentDao(jdbcTemplate)

  @Nested
  inner class RetrieveBy {

    @Test
    fun `error on repository operation`() {

      val params = mapOf("id" to A_FANTA_TOURNAMENT_ID)

      val expectedMessage = "Ops, I need help!"

      every {
        jdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_QUERY, params, fantaTournamentRowMapper)
      } throws RuntimeException(expectedMessage)

      assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.retrieveBy(A_FANTA_TOURNAMENT_ID) }
    }

    @Test
    fun `no results returned by the query`() {

      val params = mapOf("id" to A_FANTA_TOURNAMENT_ID)

      val expectedMessage = "No fanta tournament found with id $A_FANTA_TOURNAMENT_ID"

      every { jdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_QUERY, params, fantaTournamentRowMapper) } returns null

      assertThrowsWithMessage<EmptyResultDataAccessException>(expectedMessage) { dao.retrieveBy(A_FANTA_TOURNAMENT_ID) }
    }

    @Test
    fun `results retrieved successfully`() {

      val params = mapOf("id" to A_FANTA_TOURNAMENT_ID)

      val expected = aJdbcFantaTournamentDto()

      every { jdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_QUERY, params, fantaTournamentRowMapper) } returns expected

      assertThat(dao.retrieveBy(A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)
    }
  }

  @Nested
  inner class RetrieveAll {

    @Test
    fun `error on repository operation`() {

      val expectedMessage = "Ops, I need help!"

      every { jdbcTemplate.query(RETRIEVE_ALL_FANTA_TOURNAMENT_QUERY, fantaTournamentRowMapper) } throws RuntimeException(expectedMessage)

      assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.retrieveAll() }
    }

    @Test
    fun `results retrieved successfully`() {

      val fantaTournament1 = aJdbcFantaTournamentDto()
      val fantaTournament2 = aJdbcFantaTournamentDto()
      val expected = listOf(fantaTournament1, fantaTournament2)

      every { jdbcTemplate.query(RETRIEVE_ALL_FANTA_TOURNAMENT_QUERY, fantaTournamentRowMapper) } returns expected

      assertThat(dao.retrieveAll()).isEqualTo(expected)
    }
  }

  companion object {

    private const val A_FANTA_TOURNAMENT_ID = 123

    private val RETRIEVE_FANTA_TOURNAMENT_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS 
      WHERE FANTA_TOURNAMENT_ID = :id;
    """.trimIndent()

    private val RETRIEVE_ALL_FANTA_TOURNAMENT_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS; 
    """.trimIndent()
  }
}