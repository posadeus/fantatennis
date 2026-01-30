package com.posadeus.fantatennis.infrastructure.repository.database

import com.posadeus.fantatennis.domain.exception.InvalidAddPlayersException
import com.posadeus.fantatennis.domain.infrastructure.PersistTeamPlayersRepository
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcTeamDto.aJdbcTeamDto
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SqlPersistTeamPlayersRepositoryTest {

  private val teamDao: TeamDao = mockk()

  private val repository: PersistTeamPlayersRepository = SqlPersistTeamPlayersRepository(teamDao)

  @Test
  fun `not all players have been added to the team`() {

    val playerIds = setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID)

    val team1 = aJdbcTeamDto(teamId = A_TEAM_ID, playerId = A_PLAYER_ID, startingTournamentId = A_TOURNAMENT_ID)
    val team2 = aJdbcTeamDto(teamId = A_TEAM_ID, playerId = ANOTHER_PLAYER_ID, startingTournamentId = A_TOURNAMENT_ID)
    val teamsDto = setOf(team1, team2)

    val exceptionMessage = "Players [ANOTHER_PLAYER_ID] not inserted."
    val expectedMessage = "Unexpected error during insert: Players [ANOTHER_PLAYER_ID] not inserted. - Operation reverted."

    every { teamDao.persist(teamsDto) } throws InvalidAddPlayersException(exceptionMessage)

    assertThrowsWithMessage<InvalidAddPlayersException>(expectedMessage) { repository.persist(A_TEAM_ID, playerIds, A_TOURNAMENT_ID) }
  }

  @Test
  fun `players are all found and added to the team`() {

    val playerIds = setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID)

    val team1 = aJdbcTeamDto(teamId = A_TEAM_ID, playerId = A_PLAYER_ID, startingTournamentId = A_TOURNAMENT_ID)
    val team2 = aJdbcTeamDto(teamId = A_TEAM_ID, playerId = ANOTHER_PLAYER_ID, startingTournamentId = A_TOURNAMENT_ID)
    val teamsDto = setOf(team1, team2)

    every { teamDao.persist(teamsDto) } returns Unit

    assertThat(repository.persist(A_TEAM_ID, playerIds, A_TOURNAMENT_ID)).isEqualTo(Unit)

    verify(exactly = 1) { teamDao.persist(teamsDto) }
  }

  companion object {

    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val A_TEAM_ID = 1
    private const val A_TOURNAMENT_ID = 1234
  }
}