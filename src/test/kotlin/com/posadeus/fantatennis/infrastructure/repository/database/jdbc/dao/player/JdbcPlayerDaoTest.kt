package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.player

import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto.Companion.playerRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcPlayerDto.aJdbcPlayerDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate

class JdbcPlayerDaoTest {

  private val jdbcTemplate: JdbcTemplate = mockk()

  private val dao: PlayerDao = JdbcPlayerDao(jdbcTemplate)

  @Test
  fun `error on repository operation`() {

    val expectedMessage = "Ops, I need help!"

    every { jdbcTemplate.query(RETRIEVE_PLAYERS_QUERY, playerRowMapper) } throws RuntimeException(expectedMessage)

    assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.retrieveAll() }
  }

  @Test
  fun `no results returned by the query`() {

    val expected = emptyList<JdbcPlayerDto>()

    every { jdbcTemplate.query(RETRIEVE_PLAYERS_QUERY, playerRowMapper) } returns expected

    assertThat(dao.retrieveAll()).isEqualTo(expected)
  }

  @Test
  fun `results retrieved successfully`() {

    val expected = listOf(aJdbcPlayerDto(), aJdbcPlayerDto(), aJdbcPlayerDto())

    every { jdbcTemplate.query(RETRIEVE_PLAYERS_QUERY, playerRowMapper) } returns expected

    assertThat(dao.retrieveAll()).isEqualTo(expected)
  }

  companion object {

    private val RETRIEVE_PLAYERS_QUERY = """
      SELECT *
      FROM PLAYERS;
    """.trimIndent()
  }
}