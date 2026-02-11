package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.exception.InvalidPlayerPointsException
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.AtpPlayer
import com.posadeus.fantatennis.domain.model.FailureReason.PERSISTENCE_ERROR
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceFailure
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceSucceeded
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcPlayerPointsDto.aJdbcPlayerPointsDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class JdbcPersistPlayersPointsRepositoryTest {

  private val playerPointsDao: PlayerPointsDao = mockk()

  private val repository: PersistPlayersPointsRepository = JdbcPersistPlayersPointsRepository(playerPointsDao)

  @Test
  fun `not all players are persisted`() {

    val player1 = AtpPlayer(id = A_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 10.0,
                                                                                       ANOTHER_TOURNAMENT to 7.0)))
    val player2 = AtpPlayer(id = ANOTHER_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 13.0),
                                                                             ANOTHER_YEAR to mapOf(A_TOURNAMENT_ID to 5.5)))
    val player3 = AtpPlayer(id = A_THIRD_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 1.2)))
    val players = setOf(player1, player2, player3)

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

    val errorMessage = "Something went wrong, I couldn't do nothing!"

    every { playerPointsDao.persistAll(playersPointsDto) } throws InvalidPlayerPointsException(errorMessage)

    assertThat(repository.persistAll(players)).isEqualTo(FantaPointPersistenceFailure(PERSISTENCE_ERROR))

    verify(exactly = 1) { playerPointsDao.persistAll(playersPointsDto) }
  }

  @Test
  fun `exception during operation`() {

    val player1 = AtpPlayer(id = A_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 10.0)))
    val player2 = AtpPlayer(id = ANOTHER_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 13.0)))
    val player3 = AtpPlayer(id = A_THIRD_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 1.2)))
    val players = setOf(player1, player2, player3)

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

    every { playerPointsDao.persistAll(playersPointsDto) } throws RuntimeException("I'm an error.")

    assertThat(repository.persistAll(players)).isEqualTo(FantaPointPersistenceFailure(PERSISTENCE_ERROR))

    verify(exactly = 1) { playerPointsDao.persistAll(playersPointsDto) }
  }

  @Test
  fun `persist all players`() {

    val player1 = AtpPlayer(id = A_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 10.0,
                                                                                       ANOTHER_TOURNAMENT to 7.0)))
    val player2 = AtpPlayer(id = ANOTHER_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 13.0),
                                                                             ANOTHER_YEAR to mapOf(A_TOURNAMENT_ID to 5.5)))
    val player3 = AtpPlayer(id = A_THIRD_PLAYER_ID, tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 1.2)))
    val players = setOf(player1, player2, player3)

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

    every { playerPointsDao.persistAll(playersPointsDto) } returns Unit

    assertThat(repository.persistAll(players)).isEqualTo(FantaPointPersistenceSucceeded)

    verify(exactly = 1) { playerPointsDao.persistAll(playersPointsDto) }
  }

  companion object {

    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val A_THIRD_PLAYER_ID = "A_THIRD_PLAYER_ID"
    private const val A_YEAR = 2025
    private const val ANOTHER_YEAR = 2024
    private const val A_TOURNAMENT_ID = 123
    private const val ANOTHER_TOURNAMENT = 222
  }
}