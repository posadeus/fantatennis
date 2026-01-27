package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.exception.InvalidAddPlayersException
import com.posadeus.fantatennis.domain.infrastructure.PersistTeamPlayersRepository
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcPersistTeamPlayersRepositoryTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val repository: PersistTeamPlayersRepository = JdbcPersistTeamPlayersRepository(jdbcTemplate)

  @Test
  fun `not all players have been added to the team`() {

    val playerIds = setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID)

    val batchElement1 = mapOf("teamId" to A_TEAM_ID, "playerId" to A_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
    val batchElement2 = mapOf("teamId" to A_TEAM_ID, "playerId" to ANOTHER_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
    val paramSource = arrayOf(batchElement1, batchElement2)

    val expectedMessage = "Unexpected error during insert: Players [ANOTHER_PLAYER_ID] not inserted, operation reverted."

    every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } returns intArrayOf(1, 0)

    assertThrowsWithMessage<InvalidAddPlayersException>(expectedMessage) { repository.persist(A_TEAM_ID, playerIds, A_TOURNAMENT_ID) }
  }

  @Test
  fun `exception happens during inserts`() {

    val playerIds = setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID)

    val batchElement1 = mapOf("teamId" to A_TEAM_ID, "playerId" to A_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
    val batchElement2 = mapOf("teamId" to A_TEAM_ID, "playerId" to ANOTHER_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
    val paramSource = arrayOf(batchElement1, batchElement2)

    val expectedMessage = "Unexpected error during insert: Error"

    every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } throws RuntimeException("Error")

    assertThrowsWithMessage<InvalidAddPlayersException>(expectedMessage) { repository.persist(A_TEAM_ID, playerIds, A_TOURNAMENT_ID) }
  }

  @Test
  fun `players are all found and added to the team`() {

    val playerIds = setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID)

    val batchElement1 = mapOf("teamId" to A_TEAM_ID, "playerId" to A_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
    val batchElement2 = mapOf("teamId" to A_TEAM_ID, "playerId" to ANOTHER_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
    val paramSource = arrayOf(batchElement1, batchElement2)

    every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } returns intArrayOf(1, 1)

    assertThat(repository.persist(A_TEAM_ID, playerIds, A_TOURNAMENT_ID)).isEqualTo(Unit)
  }

  companion object {

    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val A_TEAM_ID = 1
    private const val A_TOURNAMENT_ID = 1234

    private val INSERT_PLAYERS_QUERY = """
      INSERT INTO TEAMS
      (TEAM_ID, PLAYER_ID, STARTING_TOURNAMENT)
      VALUES(:teamId, :playerId, :startingTournamentId);
    """.trimIndent()
  }
}