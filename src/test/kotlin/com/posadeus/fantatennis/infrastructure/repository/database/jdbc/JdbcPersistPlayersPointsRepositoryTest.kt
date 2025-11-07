package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.exception.InvalidPlayerPointsException
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.AtpPlayer
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import io.mockk.*
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcPersistPlayersPointsRepositoryTest {

  private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val repository: PersistPlayersPointsRepository = JdbcPersistPlayersPointsRepository(namedParameterJdbcTemplate)

  @Test
  fun `not all players are persisted`() {

    val player1 = AtpPlayer(id = A_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 10.0,
                                                                                       ANOTHER_TOURNAMENT to 7.0)))
    val player2 = AtpPlayer(id = ANOTHER_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 13.0),
                                                                             ANOTHER_YEAR to mapOf(A_TOURNAMENT_ID to 5.5)))
    val player3 = AtpPlayer(id = A_THIRD_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 1.2)))
    val players = setOf(player1, player2, player3)

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

    val expectedMessage = "Unexpected error during insert: " +
                          "PlayersPoints for playerId-tournamentId-year [A_PLAYER_ID-123-2025, ANOTHER_PLAYER_ID-123-2024] not inserted, " +
                          "operation reverted."

    every { namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_POINTS_QUERY, paramSource) } returns intArrayOf(0, 1, 1, 0, 1)

    assertThrowsWithMessage<InvalidPlayerPointsException>(expectedMessage) { repository.persistAll(players) }

    verify(exactly = 1) { namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_POINTS_QUERY, paramSource) }
  }

  @Test
  fun `exception during operation`() {

    val player1 = AtpPlayer(id = A_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 10.0)))
    val player2 = AtpPlayer(id = ANOTHER_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 13.0)))
    val player3 = AtpPlayer(id = A_THIRD_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 1.2)))
    val players = setOf(player1, player2, player3)

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

    val expectedMessage = "Unexpected error during insert: I'm an error."

    every { namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_POINTS_QUERY, paramSource) } throws RuntimeException("I'm an error.")

    assertThrowsWithMessage<InvalidPlayerPointsException>(expectedMessage) { repository.persistAll(players) }

    verify(exactly = 1) { namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_POINTS_QUERY, paramSource) }
  }

  @Test
  fun `persist all players`() {

    val player1 = AtpPlayer(id = A_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 10.0,
                                                                                       ANOTHER_TOURNAMENT to 7.0)))
    val player2 = AtpPlayer(id = ANOTHER_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 13.0),
                                                                             ANOTHER_YEAR to mapOf(A_TOURNAMENT_ID to 5.5)))
    val player3 = AtpPlayer(id = A_THIRD_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 1.2)))
    val players = setOf(player1, player2, player3)

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

    every { namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_POINTS_QUERY, paramSource) } returns intArrayOf(1, 1, 1, 1, 1)

    repository.persistAll(players)


    verify(exactly = 1) { namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_POINTS_QUERY, paramSource) }
  }

  companion object {

    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val A_THIRD_PLAYER_ID = "A_THIRD_PLAYER_ID"
    private const val A_YEAR = 2025
    private const val ANOTHER_YEAR = 2024
    private const val A_TOURNAMENT_ID = 123
    private const val ANOTHER_TOURNAMENT = 222

    private val INSERT_PLAYERS_POINTS_QUERY = """
      INSERT INTO PLAYERS_POINTS
      (TOURNAMENT_YEAR, TOURNAMENT_ID, PLAYER_ID, FANTA_POINTS)
      VALUES(:tournamentYear, :tournamentId, :playerId, :fantaPoints)
      ON DUPLICATE KEY UPDATE
      FANTA_POINTS = VALUES(FANTA_POINTS);
    """.trimIndent()
  }
}