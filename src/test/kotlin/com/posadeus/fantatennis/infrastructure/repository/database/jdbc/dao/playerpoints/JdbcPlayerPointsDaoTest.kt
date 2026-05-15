package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints

import com.posadeus.fantatennis.domain.exception.InvalidPlayerPointsException
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto.Companion.playersPointsRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcPlayerPointsDto.aJdbcPlayerPointsDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcPlayerPointsDaoTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val dao: PlayerPointsDao = JdbcPlayerPointsDao(jdbcTemplate)

  @Nested
  inner class RetrieveByTournamentId {

    @Test
    fun `error on repository operation`() {

      val params = mapOf("tournamentId" to A_TOURNAMENT_ID)

      val expectedMessage = "Ops, I need help!"

      every {
        jdbcTemplate.query(RETRIEVE_PLAYERS_POINTS_BY_TOURNAMENT_ID_QUERY, params, playersPointsRowMapper)
      } throws RuntimeException(expectedMessage)

      assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.retrieveByTournamentId(A_TOURNAMENT_ID) }
    }

    @Test
    fun `no results returned by the query`() {

      val params = mapOf("tournamentId" to A_TOURNAMENT_ID)

      val expected = emptyList<JdbcPlayerPointsDto>()

      every { jdbcTemplate.query(RETRIEVE_PLAYERS_POINTS_BY_TOURNAMENT_ID_QUERY, params, playersPointsRowMapper) } returns expected

      assertThat(dao.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)
    }

    @Test
    fun `results retrieved successfully`() {

      val params = mapOf("tournamentId" to A_TOURNAMENT_ID)

      val expected = listOf(aJdbcPlayerPointsDto(), aJdbcPlayerPointsDto(), aJdbcPlayerPointsDto())

      every { jdbcTemplate.query(RETRIEVE_PLAYERS_POINTS_BY_TOURNAMENT_ID_QUERY, params, playersPointsRowMapper) } returns expected

      assertThat(dao.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)
    }
  }

  @Nested
  inner class RetrieveByTournamentYear {

    @Test
    fun `error on repository operation`() {

      val params = mapOf("tournamentYear" to A_YEAR)

      val expectedMessage = "Ops, I need help!"

      every {
        jdbcTemplate.query(RETRIEVE_PLAYERS_POINTS_BY_TOURNAMENT_YEAR_QUERY, params, playersPointsRowMapper)
      } throws RuntimeException(expectedMessage)

      assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.retrieveByTournamentYear(A_YEAR) }
    }

    @Test
    fun `no results returned by the query`() {

      val params = mapOf("tournamentYear" to A_YEAR)

      val expected = emptyList<JdbcPlayerPointsDto>()

      every { jdbcTemplate.query(RETRIEVE_PLAYERS_POINTS_BY_TOURNAMENT_YEAR_QUERY, params, playersPointsRowMapper) } returns expected

      assertThat(dao.retrieveByTournamentYear(A_YEAR)).isEqualTo(expected)
    }

    @Test
    fun `results retrieved successfully`() {

      val params = mapOf("tournamentYear" to A_YEAR)

      val expected = listOf(aJdbcPlayerPointsDto(tournamentYear = A_YEAR), aJdbcPlayerPointsDto(tournamentYear = A_YEAR))

      every { jdbcTemplate.query(RETRIEVE_PLAYERS_POINTS_BY_TOURNAMENT_YEAR_QUERY, params, playersPointsRowMapper) } returns expected

      assertThat(dao.retrieveByTournamentYear(A_YEAR)).isEqualTo(expected)
    }
  }

  @Nested
  inner class PersistAll {

    @Test
    fun `not all players are persisted`() {

      val playerPointsDto1 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                                  tournamentId = A_TOURNAMENT_ID,
                                                  playerId = A_PLAYER_ID,
                                                  fantaPoints = 10.0)
      val playerPointsDto2 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                                  tournamentId = ANOTHER_TOURNAMENT,
                                                  playerId = A_PLAYER_ID,
                                                  fantaPoints = 7.0)
      val playerPointsDto3 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                                  tournamentId = A_TOURNAMENT_ID,
                                                  playerId = ANOTHER_PLAYER_ID,
                                                  fantaPoints = 13.0)
      val playerPointsDto4 = aJdbcPlayerPointsDto(tournamentYear = ANOTHER_YEAR,
                                                  tournamentId = A_TOURNAMENT_ID,
                                                  playerId = ANOTHER_PLAYER_ID,
                                                  fantaPoints = 5.5)
      val playerPointsDto5 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                                  tournamentId = A_TOURNAMENT_ID,
                                                  playerId = A_THIRD_PLAYER_ID,
                                                  fantaPoints = 1.2)
      val playersPointsDto = listOf(playerPointsDto1, playerPointsDto2, playerPointsDto3, playerPointsDto4, playerPointsDto5)

      val batchElement1 = mapOf("tournamentYear" to A_YEAR,
                                "tournamentId" to A_TOURNAMENT_ID,
                                "playerId" to A_PLAYER_ID,
                                "fantaPoints" to 10.0)
      val batchElement2 = mapOf("tournamentYear" to A_YEAR,
                                "tournamentId" to ANOTHER_TOURNAMENT,
                                "playerId" to A_PLAYER_ID,
                                "fantaPoints" to 7.0)
      val batchElement3 = mapOf("tournamentYear" to A_YEAR,
                                "tournamentId" to A_TOURNAMENT_ID,
                                "playerId" to ANOTHER_PLAYER_ID,
                                "fantaPoints" to 13.0)
      val batchElement4 = mapOf("tournamentYear" to ANOTHER_YEAR,
                                "tournamentId" to A_TOURNAMENT_ID,
                                "playerId" to ANOTHER_PLAYER_ID,
                                "fantaPoints" to 5.5)
      val batchElement5 = mapOf("tournamentYear" to A_YEAR,
                                "tournamentId" to A_TOURNAMENT_ID,
                                "playerId" to A_THIRD_PLAYER_ID,
                                "fantaPoints" to 1.2)
      val paramSource = arrayOf(batchElement1, batchElement2, batchElement3, batchElement4, batchElement5)

      val expectedMessage = "PlayersPoints for playerId-tournamentId-year [A_PLAYER_ID-123-2025, ANOTHER_PLAYER_ID-123-2024] not inserted, " +
                            "operation reverted."

      every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_POINTS_QUERY, paramSource) } returns intArrayOf(0, 1, 1, 0, 1)

      assertThrowsWithMessage<InvalidPlayerPointsException>(expectedMessage) { dao.persistAll(playersPointsDto) }

      verify(exactly = 1) { jdbcTemplate.batchUpdate(INSERT_PLAYERS_POINTS_QUERY, paramSource) }
    }

    @Test
    fun `exception during operation`() {

      val playerPointsDto1 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                                  tournamentId = A_TOURNAMENT_ID,
                                                  playerId = A_PLAYER_ID,
                                                  fantaPoints = 10.0)
      val playerPointsDto2 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                                  tournamentId = A_TOURNAMENT_ID,
                                                  playerId = ANOTHER_PLAYER_ID,
                                                  fantaPoints = 13.0)
      val playerPointsDto3 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                                  tournamentId = A_TOURNAMENT_ID,
                                                  playerId = A_THIRD_PLAYER_ID,
                                                  fantaPoints = 1.2)
      val playersPointsDto = listOf(playerPointsDto1, playerPointsDto2, playerPointsDto3)

      val batchElement1 = mapOf("tournamentYear" to A_YEAR,
                                "tournamentId" to A_TOURNAMENT_ID,
                                "playerId" to A_PLAYER_ID,
                                "fantaPoints" to 10.0)
      val batchElement2 = mapOf("tournamentYear" to A_YEAR,
                                "tournamentId" to A_TOURNAMENT_ID,
                                "playerId" to ANOTHER_PLAYER_ID,
                                "fantaPoints" to 13.0)
      val batchElement3 = mapOf("tournamentYear" to A_YEAR,
                                "tournamentId" to A_TOURNAMENT_ID,
                                "playerId" to A_THIRD_PLAYER_ID,
                                "fantaPoints" to 1.2)
      val paramSource = arrayOf(batchElement1, batchElement2, batchElement3)

      val expectedMessage = "I'm an error."

      every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_POINTS_QUERY, paramSource) } throws RuntimeException("I'm an error.")

      assertThrowsWithMessage<InvalidPlayerPointsException>(expectedMessage) { dao.persistAll(playersPointsDto) }

      verify(exactly = 1) { jdbcTemplate.batchUpdate(INSERT_PLAYERS_POINTS_QUERY, paramSource) }
    }

    @Test
    fun `persist all players`() {

      val playerPointsDto1 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                                  tournamentId = A_TOURNAMENT_ID,
                                                  playerId = A_PLAYER_ID,
                                                  fantaPoints = 10.0)
      val playerPointsDto2 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                                  tournamentId = ANOTHER_TOURNAMENT,
                                                  playerId = A_PLAYER_ID,
                                                  fantaPoints = 7.0)
      val playerPointsDto3 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                                  tournamentId = A_TOURNAMENT_ID,
                                                  playerId = ANOTHER_PLAYER_ID,
                                                  fantaPoints = 13.0)
      val playerPointsDto4 = aJdbcPlayerPointsDto(tournamentYear = ANOTHER_YEAR,
                                                  tournamentId = A_TOURNAMENT_ID,
                                                  playerId = ANOTHER_PLAYER_ID,
                                                  fantaPoints = 5.5)
      val playerPointsDto5 = aJdbcPlayerPointsDto(tournamentYear = A_YEAR,
                                                  tournamentId = A_TOURNAMENT_ID,
                                                  playerId = A_THIRD_PLAYER_ID,
                                                  fantaPoints = 1.2)
      val playersPointsDto = listOf(playerPointsDto1, playerPointsDto2, playerPointsDto3, playerPointsDto4, playerPointsDto5)

      val batchElement1 = mapOf("tournamentYear" to A_YEAR,
                                "tournamentId" to A_TOURNAMENT_ID,
                                "playerId" to A_PLAYER_ID,
                                "fantaPoints" to 10.0)
      val batchElement2 = mapOf("tournamentYear" to A_YEAR,
                                "tournamentId" to ANOTHER_TOURNAMENT,
                                "playerId" to A_PLAYER_ID,
                                "fantaPoints" to 7.0)
      val batchElement3 = mapOf("tournamentYear" to A_YEAR,
                                "tournamentId" to A_TOURNAMENT_ID,
                                "playerId" to ANOTHER_PLAYER_ID,
                                "fantaPoints" to 13.0)
      val batchElement4 = mapOf("tournamentYear" to ANOTHER_YEAR,
                                "tournamentId" to A_TOURNAMENT_ID,
                                "playerId" to ANOTHER_PLAYER_ID,
                                "fantaPoints" to 5.5)
      val batchElement5 = mapOf("tournamentYear" to A_YEAR,
                                "tournamentId" to A_TOURNAMENT_ID,
                                "playerId" to A_THIRD_PLAYER_ID,
                                "fantaPoints" to 1.2)
      val paramSource = arrayOf(batchElement1, batchElement2, batchElement3, batchElement4, batchElement5)

      every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_POINTS_QUERY, paramSource) } returns intArrayOf(1, 1, 1, 1, 1)

      dao.persistAll(playersPointsDto)

      verify(exactly = 1) { jdbcTemplate.batchUpdate(INSERT_PLAYERS_POINTS_QUERY, paramSource) }
    }
  }

  companion object {

    private const val A_TOURNAMENT_ID = 123
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val A_THIRD_PLAYER_ID = "A_THIRD_PLAYER_ID"
    private const val A_YEAR = 2025
    private const val ANOTHER_YEAR = 2024
    private const val ANOTHER_TOURNAMENT = 222

    private val RETRIEVE_PLAYERS_POINTS_BY_TOURNAMENT_ID_QUERY = """
      SELECT *
      FROM PLAYERS_POINTS
      WHERE TOURNAMENT_ID = :tournamentId
    """.trimIndent()

    private val RETRIEVE_PLAYERS_POINTS_BY_TOURNAMENT_YEAR_QUERY = """
      SELECT *
      FROM PLAYERS_POINTS
      WHERE TOURNAMENT_YEAR = :tournamentYear
    """.trimIndent()

    private val INSERT_PLAYERS_POINTS_QUERY = """
      INSERT INTO PLAYERS_POINTS
      (TOURNAMENT_YEAR, TOURNAMENT_ID, PLAYER_ID, FANTA_POINTS)
      VALUES(:tournamentYear, :tournamentId, :playerId, :fantaPoints)
      ON DUPLICATE KEY UPDATE
      FANTA_POINTS = VALUES(FANTA_POINTS);
    """.trimIndent()
  }
}