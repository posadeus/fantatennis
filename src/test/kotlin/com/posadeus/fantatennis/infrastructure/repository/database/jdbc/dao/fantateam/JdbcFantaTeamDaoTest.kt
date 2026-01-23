package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantateam

import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto.Companion.fantaTeamRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcFantaTeamDto.aJdbcFantaTeamDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcFantaTeamDaoTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val dao: FantaTeamDao = JdbcFantaTeamDao(jdbcTemplate)

  @Test
  fun `error on repository operation`() {

    val params = mapOf("teamId" to A_TEAM_ID)

    val expectedMessage = "Ops, I need help!"

    every { jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, params, fantaTeamRowMapper) } throws RuntimeException(expectedMessage)

    assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.retrieveBy(A_TEAM_ID) }
  }

  @Test
  fun `no results returned by the query`() {

    val params = mapOf("teamId" to A_TEAM_ID)

    val expectedMessage = "No fanta team found with id $A_TEAM_ID"

    every { jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, params, fantaTeamRowMapper) } returns null

    assertThrowsWithMessage<EmptyResultDataAccessException>(expectedMessage) { dao.retrieveBy(A_TEAM_ID) }
  }

  @Test
  fun `results retrieved successfully`() {

    val params = mapOf("teamId" to A_TEAM_ID)

    val expected = aJdbcFantaTeamDto()

    every { jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, params, fantaTeamRowMapper) } returns expected

    assertThat(dao.retrieveBy(A_TEAM_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TEAM_ID = 123

    private val RETRIEVE_FANTA_TEAM_QUERY = """
      SELECT *
      FROM FANTA_TEAMS
      WHERE TEAM_ID = :teamId;
    """.trimIndent()
  }
}