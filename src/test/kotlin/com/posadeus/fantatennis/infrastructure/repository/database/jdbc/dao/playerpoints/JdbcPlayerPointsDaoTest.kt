package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints

import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto.Companion.playersPointsRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcPlayerPointsDto.aJdbcPlayerPointsDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcPlayerPointsDaoTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val dao: PlayerPointsDao = JdbcPlayerPointsDao(jdbcTemplate)

  @Test
  fun `error on repository operation`() {

    val params = mapOf("tournamentId" to A_TOURNAMENT_ID)

    val expectedMessage = "Ops, I need help!"

    every { jdbcTemplate.query(RETRIEVE_PLAYERS_POINTS_QUERY, params, playersPointsRowMapper) } throws RuntimeException(expectedMessage)

    assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.retrieveByTournamentId(A_TOURNAMENT_ID) }
  }

  @Test
  fun `no results returned by the query`() {

    val params = mapOf("tournamentId" to A_TOURNAMENT_ID)

    val expected = emptyList<JdbcPlayerPointsDto>()

    every { jdbcTemplate.query(RETRIEVE_PLAYERS_POINTS_QUERY, params, playersPointsRowMapper) } returns expected

    assertThat(dao.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `results retrieved successfully`() {

    val params = mapOf("tournamentId" to A_TOURNAMENT_ID)

    val expected = listOf(aJdbcPlayerPointsDto(), aJdbcPlayerPointsDto(), aJdbcPlayerPointsDto())

    every { jdbcTemplate.query(RETRIEVE_PLAYERS_POINTS_QUERY, params, playersPointsRowMapper) } returns expected

    assertThat(dao.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1

    private val RETRIEVE_PLAYERS_POINTS_QUERY = """
      SELECT *
      FROM PLAYERS_POINTS
      WHERE TOURNAMENT_ID = :tournamentId
    """.trimIndent()
  }
}