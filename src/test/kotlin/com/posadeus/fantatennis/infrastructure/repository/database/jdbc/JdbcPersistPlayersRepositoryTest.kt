package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.exception.InvalidPlayerException
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcPersistPlayersRepositoryTest {

  private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val repository: PersistPlayersRepository = JdbcPersistPlayersRepository(namedParameterJdbcTemplate)

  @Test
  fun `not all players are persisted`() {

    val player1 = DomainPlayer(id = A_PLAYER_ID, atpId = AN_ATP_PLAYER_ID, fullName = A_FULL_NAME)
    val player2 = DomainPlayer(id = ANOTHER_PLAYER_ID, atpId = ANOTHER_ATP_PLAYER_ID, fullName = ANOTHER_FULL_NAME)
    val player3 = DomainPlayer(id = A_THIRD_PLAYER_ID, atpId = A_THIRD_ATP_PLAYER_ID, fullName = A_THIRD_FULL_NAME)
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

    val expectedMessage = "Unexpected error during insert: Players [A_PLAYER_ID, A_THIRD_PLAYER_ID] not inserted, operation reverted."

    every { namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } returns intArrayOf(0, 1, 0)

    assertThrowsWithMessage<InvalidPlayerException>(expectedMessage) { repository.persistAll(players) }

    verify(exactly = 1) { namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) }
  }

  @Test
  fun `exception during operation`() {

    val player1 = DomainPlayer(id = A_PLAYER_ID, atpId = AN_ATP_PLAYER_ID, fullName = A_FULL_NAME)
    val player2 = DomainPlayer(id = ANOTHER_PLAYER_ID, atpId = ANOTHER_ATP_PLAYER_ID, fullName = ANOTHER_FULL_NAME)
    val player3 = DomainPlayer(id = A_THIRD_PLAYER_ID, atpId = A_THIRD_ATP_PLAYER_ID, fullName = A_THIRD_FULL_NAME)
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

    val expectedMessage = "Unexpected error during insert: I'm an error."

    every { namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } throws RuntimeException("I'm an error.")

    assertThrowsWithMessage<InvalidPlayerException>(expectedMessage) { repository.persistAll(players) }

    verify(exactly = 1) { namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) }
  }

  @Test
  fun `persist all players`() {

    val player1 = DomainPlayer(id = A_PLAYER_ID, atpId = AN_ATP_PLAYER_ID, fullName = A_FULL_NAME)
    val player2 = DomainPlayer(id = ANOTHER_PLAYER_ID, atpId = ANOTHER_ATP_PLAYER_ID, fullName = ANOTHER_FULL_NAME)
    val player3 = DomainPlayer(id = A_THIRD_PLAYER_ID, atpId = A_THIRD_ATP_PLAYER_ID, fullName = A_THIRD_FULL_NAME)
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

    every { namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } returns intArrayOf(1, 1, 1)

    repository.persistAll(players)


    verify(exactly = 1) { namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) }
  }

  private inline fun <reified T : Throwable> assertThrowsWithMessage(expectedMessage: String, block: () -> Unit) {

    val exception = assertThrows<T> { block() }

    assertThat(exception.message).isEqualTo(expectedMessage)
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
  }
}