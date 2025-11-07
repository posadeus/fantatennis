package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.player

import com.posadeus.fantatennis.domain.exception.InvalidPlayerException
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto.Companion.playerRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcPlayerDto.aJdbcPlayerDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcPlayerDaoTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val dao: PlayerDao = JdbcPlayerDao(jdbcTemplate)

  @Nested
  inner class Retrieve {

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
  }

  @Nested
  inner class Persist {

    @Test
    fun `not all players are persisted`() {

      val player1 = JdbcPlayerDto(playerId = A_PLAYER_ID, atpTourId = AN_ATP_PLAYER_ID, fullName = A_FULL_NAME)
      val player2 = JdbcPlayerDto(playerId = ANOTHER_PLAYER_ID, atpTourId = ANOTHER_ATP_PLAYER_ID, fullName = ANOTHER_FULL_NAME)
      val player3 = JdbcPlayerDto(playerId = A_THIRD_PLAYER_ID, atpTourId = A_THIRD_ATP_PLAYER_ID, fullName = A_THIRD_FULL_NAME)
      val players = setOf(player1, player2, player3)

      val batchElement1 = mapOf("playerId" to A_PLAYER_ID,
                                "atpTourId" to AN_ATP_PLAYER_ID,
                                "fullName" to A_FULL_NAME)
      val batchElement2 = mapOf("playerId" to ANOTHER_PLAYER_ID,
                                "atpTourId" to ANOTHER_ATP_PLAYER_ID,
                                "fullName" to ANOTHER_FULL_NAME)
      val batchElement3 = mapOf("playerId" to A_THIRD_PLAYER_ID,
                                "atpTourId" to A_THIRD_ATP_PLAYER_ID,
                                "fullName" to A_THIRD_FULL_NAME)
      val paramSource = arrayOf(batchElement1, batchElement2, batchElement3)

      val expectedMessage = "Players [A_PLAYER_ID, A_THIRD_PLAYER_ID] not inserted, operation reverted."

      every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } returns intArrayOf(0, 1, 0)

      assertThrowsWithMessage<InvalidPlayerException>(expectedMessage) { dao.persistAll(players) }

      verify(exactly = 1) { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) }
    }

    @Test
    fun `exception during operation`() {

      val player1 = JdbcPlayerDto(playerId = A_PLAYER_ID, atpTourId = AN_ATP_PLAYER_ID, fullName = A_FULL_NAME)
      val player2 = JdbcPlayerDto(playerId = ANOTHER_PLAYER_ID, atpTourId = ANOTHER_ATP_PLAYER_ID, fullName = ANOTHER_FULL_NAME)
      val player3 = JdbcPlayerDto(playerId = A_THIRD_PLAYER_ID, atpTourId = A_THIRD_ATP_PLAYER_ID, fullName = A_THIRD_FULL_NAME)
      val players = setOf(player1, player2, player3)

      val batchElement1 = mapOf("playerId" to A_PLAYER_ID,
                                "atpTourId" to AN_ATP_PLAYER_ID,
                                "fullName" to A_FULL_NAME)
      val batchElement2 = mapOf("playerId" to ANOTHER_PLAYER_ID,
                                "atpTourId" to ANOTHER_ATP_PLAYER_ID,
                                "fullName" to ANOTHER_FULL_NAME)
      val batchElement3 = mapOf("playerId" to A_THIRD_PLAYER_ID,
                                "atpTourId" to A_THIRD_ATP_PLAYER_ID,
                                "fullName" to A_THIRD_FULL_NAME)
      val paramSource = arrayOf(batchElement1, batchElement2, batchElement3)

      val expectedMessage = "I'm an error."

      every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } throws RuntimeException(expectedMessage)

      assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.persistAll(players) }

      verify(exactly = 1) { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) }
    }

    @Test
    fun `persist all players`() {

      val player1 = JdbcPlayerDto(playerId = A_PLAYER_ID, atpTourId = AN_ATP_PLAYER_ID, fullName = A_FULL_NAME)
      val player2 = JdbcPlayerDto(playerId = ANOTHER_PLAYER_ID, atpTourId = ANOTHER_ATP_PLAYER_ID, fullName = ANOTHER_FULL_NAME)
      val player3 = JdbcPlayerDto(playerId = A_THIRD_PLAYER_ID, atpTourId = A_THIRD_ATP_PLAYER_ID, fullName = A_THIRD_FULL_NAME)
      val players = setOf(player1, player2, player3)

      val batchElement1 = mapOf("playerId" to A_PLAYER_ID,
                                "atpTourId" to AN_ATP_PLAYER_ID,
                                "fullName" to A_FULL_NAME)
      val batchElement2 = mapOf("playerId" to ANOTHER_PLAYER_ID,
                                "atpTourId" to ANOTHER_ATP_PLAYER_ID,
                                "fullName" to ANOTHER_FULL_NAME)
      val batchElement3 = mapOf("playerId" to A_THIRD_PLAYER_ID,
                                "atpTourId" to A_THIRD_ATP_PLAYER_ID,
                                "fullName" to A_THIRD_FULL_NAME)
      val paramSource = arrayOf(batchElement1, batchElement2, batchElement3)

      every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } returns intArrayOf(1, 1, 1)

      dao.persistAll(players)

      verify(exactly = 1) { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) }
    }
  }

  companion object {

    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val AN_ATP_PLAYER_ID = "AN_ATP_PLAYER_ID"
    private const val A_FULL_NAME = "A_FULL_NAME"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val ANOTHER_ATP_PLAYER_ID = "ANOTHER_ATP_PLAYER_ID"
    private const val ANOTHER_FULL_NAME = "ANOTHER_FULL_NAME"
    private const val A_THIRD_PLAYER_ID = "A_THIRD_PLAYER_ID"
    private const val A_THIRD_ATP_PLAYER_ID = "A_THIRD_ATP_PLAYER_ID"
    private const val A_THIRD_FULL_NAME = "A_THIRD_FULL_NAME"

    private val INSERT_PLAYERS_QUERY = """
        INSERT INTO PLAYERS
        (PLAYER_ID, ATP_TOUR_ID, FULL_NAME)
        VALUES(:playerId, :atpTourId, :fullName);
      """.trimIndent()

    private val RETRIEVE_PLAYERS_QUERY = """
        SELECT *
        FROM PLAYERS;
      """.trimIndent()
  }
}