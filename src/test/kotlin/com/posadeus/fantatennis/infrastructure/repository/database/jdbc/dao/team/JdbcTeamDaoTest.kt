package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.team

import com.posadeus.fantatennis.domain.exception.InvalidAddPlayersException
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto.Companion.teamRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcTeamDto.aJdbcTeamDto
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcTeamDaoTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val dao: TeamDao = JdbcTeamDao(jdbcTemplate)

  @Nested
  inner class Persist {

    @Test
    fun `not all players have been added to the team`() {

      val team1 = aJdbcTeamDto(teamId = A_TEAM_ID, playerId = A_PLAYER_ID, startingTournamentId = A_TOURNAMENT_ID)
      val team2 = aJdbcTeamDto(teamId = A_TEAM_ID, playerId = ANOTHER_PLAYER_ID, startingTournamentId = A_TOURNAMENT_ID)
      val teamsDto = setOf(team1, team2)

      val batchElement1 = mapOf("teamId" to A_TEAM_ID, "playerId" to A_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
      val batchElement2 = mapOf("teamId" to A_TEAM_ID, "playerId" to ANOTHER_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
      val paramSource = arrayOf(batchElement1, batchElement2)

      val expectedMessage = "Players [ANOTHER_PLAYER_ID] not inserted."

      every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } returns intArrayOf(1, 0)

      assertThrowsWithMessage<InvalidAddPlayersException>(expectedMessage) { dao.persist(teamsDto) }

      verify(exactly = 1) { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) }
    }

    @Test
    fun `exception happens during inserts`() {

      val team1 = aJdbcTeamDto(teamId = A_TEAM_ID, playerId = A_PLAYER_ID, startingTournamentId = A_TOURNAMENT_ID)
      val team2 = aJdbcTeamDto(teamId = A_TEAM_ID, playerId = ANOTHER_PLAYER_ID, startingTournamentId = A_TOURNAMENT_ID)
      val teamsDto = setOf(team1, team2)

      val batchElement1 = mapOf("teamId" to A_TEAM_ID, "playerId" to A_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
      val batchElement2 = mapOf("teamId" to A_TEAM_ID, "playerId" to ANOTHER_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
      val paramSource = arrayOf(batchElement1, batchElement2)

      val expectedMessage = "Error"

      every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } throws RuntimeException(expectedMessage)

      assertThrowsWithMessage<InvalidAddPlayersException>(expectedMessage) { dao.persist(teamsDto) }

      verify(exactly = 1) { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) }
    }

    @Test
    fun `players are all found and added to the team`() {

      val team1 = aJdbcTeamDto(teamId = A_TEAM_ID, playerId = A_PLAYER_ID, startingTournamentId = A_TOURNAMENT_ID)
      val team2 = aJdbcTeamDto(teamId = A_TEAM_ID, playerId = ANOTHER_PLAYER_ID, startingTournamentId = A_TOURNAMENT_ID)
      val teamsDto = setOf(team1, team2)

      val batchElement1 = mapOf("teamId" to A_TEAM_ID, "playerId" to A_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
      val batchElement2 = mapOf("teamId" to A_TEAM_ID, "playerId" to ANOTHER_PLAYER_ID, "startingTournamentId" to A_TOURNAMENT_ID)
      val paramSource = arrayOf(batchElement1, batchElement2)

      every { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) } returns intArrayOf(1, 1)

      assertThat(dao.persist(teamsDto)).isEqualTo(Unit)

      verify(exactly = 1) { jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, paramSource) }
    }
  }

  @Nested
  inner class RetrieveByTeamIds {

    @Test
    fun `teams not found`() {

      val teamIds = setOf(A_TEAM_ID, ANOTHER_TEAM_ID)

      val params = mapOf("teamIds" to teamIds)

      val expected = emptyList<JdbcTeamDto>()

      every { jdbcTemplate.query(RETRIEVE_TEAMS_QUERY, params, teamRowMapper) } returns expected

      assertThat(dao.retrieveBy(teamIds)).isEqualTo(expected)
    }

    @Test
    fun `teams found`() {

      val teamIds = setOf(A_TEAM_ID, ANOTHER_TEAM_ID)

      val params = mapOf("teamIds" to teamIds)

      val expected = listOf(aJdbcTeamDto(teamId = A_TEAM_ID), aJdbcTeamDto(teamId = ANOTHER_TEAM_ID))

      every { jdbcTemplate.query(RETRIEVE_TEAMS_QUERY, params, teamRowMapper) } returns expected

      assertThat(dao.retrieveBy(teamIds)).isEqualTo(expected)
    }
  }

  companion object {

    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val A_TEAM_ID = 1
    private const val ANOTHER_TEAM_ID = 2
    private const val A_TOURNAMENT_ID = 1234

    private val INSERT_PLAYERS_QUERY = """
      INSERT INTO TEAMS
      (TEAM_ID, PLAYER_ID, STARTING_TOURNAMENT)
      VALUES(:teamId, :playerId, :startingTournamentId);
    """.trimIndent()

    private val RETRIEVE_TEAMS_QUERY = """
      SELECT *
      FROM TEAMS 
      WHERE TEAM_ID IN (:teamIds)
    """.trimIndent()
  }
}