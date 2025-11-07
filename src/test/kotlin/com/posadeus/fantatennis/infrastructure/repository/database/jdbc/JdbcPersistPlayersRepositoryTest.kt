package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.exception.InvalidPlayerException
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceFailure
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceSucceeded
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class JdbcPersistPlayersRepositoryTest {

  private val playerDao: PlayerDao = mockk()

  private val repository: PersistPlayersRepository = JdbcPersistPlayersRepository(playerDao)

  @Test
  fun `playerDao throws InvalidPlayerException`() {

    val player1 = DomainPlayer(id = A_PLAYER_ID, atpId = AN_ATP_PLAYER_ID, fullName = A_FULL_NAME)
    val player2 = DomainPlayer(id = ANOTHER_PLAYER_ID, atpId = ANOTHER_ATP_PLAYER_ID, fullName = ANOTHER_FULL_NAME)
    val player3 = DomainPlayer(id = A_THIRD_PLAYER_ID, atpId = A_THIRD_ATP_PLAYER_ID, fullName = A_THIRD_FULL_NAME)
    val players = setOf(player1, player2, player3)

    val jdbcPlayer1 = JdbcPlayerDto(playerId = A_PLAYER_ID, atpTourId = AN_ATP_PLAYER_ID, fullName = A_FULL_NAME)
    val jdbcPlayer2 = JdbcPlayerDto(playerId = ANOTHER_PLAYER_ID, atpTourId = ANOTHER_ATP_PLAYER_ID, fullName = ANOTHER_FULL_NAME)
    val jdbcPlayer3 = JdbcPlayerDto(playerId = A_THIRD_PLAYER_ID, atpTourId = A_THIRD_ATP_PLAYER_ID, fullName = A_THIRD_FULL_NAME)
    val jdbcPlayers = setOf(jdbcPlayer1, jdbcPlayer2, jdbcPlayer3)

    val expectedError = "You are doing something wrong!"
    val expectedMessage = "Operation failed, no players persisted."
    val expected = PlayerPersistenceFailure(message = expectedMessage, error = expectedError)

    every { playerDao.persistAll(jdbcPlayers) } throws InvalidPlayerException(expectedError)

    assertThat(repository.persistAll(players)).isEqualTo(expected)
  }

  @Test
  fun `exception during operation`() {

    val player1 = DomainPlayer(id = A_PLAYER_ID, atpId = AN_ATP_PLAYER_ID, fullName = A_FULL_NAME)
    val player2 = DomainPlayer(id = ANOTHER_PLAYER_ID, atpId = ANOTHER_ATP_PLAYER_ID, fullName = ANOTHER_FULL_NAME)
    val player3 = DomainPlayer(id = A_THIRD_PLAYER_ID, atpId = A_THIRD_ATP_PLAYER_ID, fullName = A_THIRD_FULL_NAME)
    val players = setOf(player1, player2, player3)

    val jdbcPlayer1 = JdbcPlayerDto(playerId = A_PLAYER_ID, atpTourId = AN_ATP_PLAYER_ID, fullName = A_FULL_NAME)
    val jdbcPlayer2 = JdbcPlayerDto(playerId = ANOTHER_PLAYER_ID, atpTourId = ANOTHER_ATP_PLAYER_ID, fullName = ANOTHER_FULL_NAME)
    val jdbcPlayer3 = JdbcPlayerDto(playerId = A_THIRD_PLAYER_ID, atpTourId = A_THIRD_ATP_PLAYER_ID, fullName = A_THIRD_FULL_NAME)
    val jdbcPlayers = setOf(jdbcPlayer1, jdbcPlayer2, jdbcPlayer3)

    val expectedError = "Runtime exception"
    val expectedMessage = "Insert failure, please verify your input."
    val expected = PlayerPersistenceFailure(message = expectedMessage, error = expectedError)

    every { playerDao.persistAll(jdbcPlayers) } throws RuntimeException(expectedError)

    assertThat(repository.persistAll(players)).isEqualTo(expected)
  }

  @Test
  fun `persist all players`() {

    val player1 = DomainPlayer(id = A_PLAYER_ID, atpId = AN_ATP_PLAYER_ID, fullName = A_FULL_NAME)
    val player2 = DomainPlayer(id = ANOTHER_PLAYER_ID, atpId = ANOTHER_ATP_PLAYER_ID, fullName = ANOTHER_FULL_NAME)
    val player3 = DomainPlayer(id = A_THIRD_PLAYER_ID, atpId = A_THIRD_ATP_PLAYER_ID, fullName = A_THIRD_FULL_NAME)
    val players = setOf(player1, player2, player3)

    val jdbcPlayer1 = JdbcPlayerDto(playerId = A_PLAYER_ID, atpTourId = AN_ATP_PLAYER_ID, fullName = A_FULL_NAME)
    val jdbcPlayer2 = JdbcPlayerDto(playerId = ANOTHER_PLAYER_ID, atpTourId = ANOTHER_ATP_PLAYER_ID, fullName = ANOTHER_FULL_NAME)
    val jdbcPlayer3 = JdbcPlayerDto(playerId = A_THIRD_PLAYER_ID, atpTourId = A_THIRD_ATP_PLAYER_ID, fullName = A_THIRD_FULL_NAME)
    val jdbcPlayers = setOf(jdbcPlayer1, jdbcPlayer2, jdbcPlayer3)

    val expected = PlayerPersistenceSucceeded

    every { playerDao.persistAll(jdbcPlayers) } just runs

    assertThat(repository.persistAll(players)).isEqualTo(expected)

    verify(exactly = 1) { playerDao.persistAll(jdbcPlayers) }
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
  }
}