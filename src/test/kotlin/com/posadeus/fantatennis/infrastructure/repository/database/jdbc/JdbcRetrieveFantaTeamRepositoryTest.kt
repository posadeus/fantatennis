package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamResultsDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcRetrieveFantaTeamRepositoryTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val repository: RetrieveFantaTeamRepository = JdbcRetrieveFantaTeamRepository(jdbcTemplate)

  @Test
  fun `retrieve fanta team fails due to db error on fantaTeam`() {

    val teamParams = mapOf("teamId" to A_TEAM_ID)

    val expected = ErrorTeam

    every {
      jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, teamParams, any<RowMapper<JdbcFantaTeamDto>>())
    } throws RuntimeException("I'm an error, BOOOH!")

    assertThat(repository.retrieve(A_TEAM_ID)).isEqualTo(expected)

    verify(exactly = 0) { jdbcTemplate.query(RETRIEVE_FANTA_TEAM_RESULTS_QUERY, teamParams, any<RowMapper<JdbcFantaTeamResultsDto>>()) }
  }

  @Test
  fun `fanta team not found`() {

    val teamParams = mapOf("teamId" to A_TEAM_ID)

    val expected = TeamIdNotFoundTeam

    every {
      jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, teamParams, any<RowMapper<JdbcFantaTeamDto>>())
    } throws EmptyResultDataAccessException(1)

    assertThat(repository.retrieve(A_TEAM_ID)).isEqualTo(expected)

    verify(exactly = 0) { jdbcTemplate.query(RETRIEVE_FANTA_TEAM_RESULTS_QUERY, teamParams, any<RowMapper<JdbcFantaTeamResultsDto>>()) }
  }

  @Test
  fun `retrieve fanta team fails due to db error on fantaTeamResults`() {

    val teamParams = mapOf("teamId" to A_TEAM_ID)
    val fantaTeam = JdbcFantaTeamDto(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)

    val expected = ErrorTeam

    every { jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, teamParams, any<RowMapper<JdbcFantaTeamDto>>()) } returns fantaTeam
    every {
      jdbcTemplate.query(RETRIEVE_FANTA_TEAM_RESULTS_QUERY, teamParams, any<RowMapper<JdbcFantaTeamResultsDto>>())
    } throws RuntimeException("I'm an error, BOOOH!")

    assertThat(repository.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `retrieve fanta team points for empty team`() {

    val teamParams = mapOf("teamId" to A_TEAM_ID)
    val fantaTeam = JdbcFantaTeamDto(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)
    val fantaTeamResultsDto = emptyList<JdbcFantaTeamResultsDto>()

    val expected = FoundTeam(team = TeamDto(owner = AN_OWNER_ID, players = emptyList(), totalScore = 0.00))

    every { jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, teamParams, any<RowMapper<JdbcFantaTeamDto>>()) } returns fantaTeam
    every {
      jdbcTemplate.query(RETRIEVE_FANTA_TEAM_RESULTS_QUERY, teamParams, any<RowMapper<JdbcFantaTeamResultsDto>>())
    } returns fantaTeamResultsDto

    assertThat(repository.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `retrieve fanta team succeeded`() {

    val teamParams = mapOf("teamId" to A_TEAM_ID)
    val fantaTeam = JdbcFantaTeamDto(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)
    val fantaTeamResultsDto = listOf(JdbcFantaTeamResultsDto(playerId = A_PLAYER_ID,
                                                             playerFullName = A_PLAYER_NAME,
                                                             playerTotalScore = 5.00),
                                     JdbcFantaTeamResultsDto(playerId = ANOTHER_PLAYER_ID,
                                                             playerFullName = ANOTHER_PLAYER_NAME,
                                                             playerTotalScore = 18.00),
                                     JdbcFantaTeamResultsDto(playerId = A_THIRD_PLAYER_ID,
                                                             playerFullName = A_THIRD_PLAYER_NAME,
                                                             playerTotalScore = 12.00))

    val expected = FoundTeam(team = TeamDto(owner = AN_OWNER_ID,
                                            players = listOf(PlayerPointsDto(fullName = ANOTHER_PLAYER_NAME, fantaPoints = 18.00),
                                                             PlayerPointsDto(fullName = A_THIRD_PLAYER_NAME, fantaPoints = 12.00),
                                                             PlayerPointsDto(fullName = A_PLAYER_NAME, fantaPoints = 5.00)),
                                            totalScore = 35.00))

    every { jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, teamParams, any<RowMapper<JdbcFantaTeamDto>>()) } returns fantaTeam
    every {
      jdbcTemplate.query(RETRIEVE_FANTA_TEAM_RESULTS_QUERY, teamParams, any<RowMapper<JdbcFantaTeamResultsDto>>())
    } returns fantaTeamResultsDto

    assertThat(repository.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TEAM_ID = 1234
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val A_THIRD_PLAYER_ID = "A_THIRD_PLAYER_ID"
    private const val A_PLAYER_NAME = "A_PLAYER_NAME"
    private const val ANOTHER_PLAYER_NAME = "ANOTHER_PLAYER_NAME"
    private const val A_THIRD_PLAYER_NAME = "A_THIRD_PLAYER_NAME"
    private const val AN_OWNER_ID = "AN_OWNER_ID"

    private val RETRIEVE_FANTA_TEAM_QUERY = """
      SELECT TEAM_ID, OWNER_ID
      FROM FANTA_TEAMS
      WHERE TEAM_ID = :teamId;
    """.trimIndent()

    private val RETRIEVE_FANTA_TEAM_RESULTS_QUERY = """
      SELECT 
        pointPerPlayerByTournament.OWNER_ID,
        playerStandsForTeam.PLAYER_ID, 
        playerStandsForTeam.FULL_NAME, 
        SUM(pointPerPlayerByTournament.FANTA_POINTS) AS TOTAL_SCORE
      FROM
      (
      	SELECT 
          t.TEAM_ID, 
          t.PLAYER_ID, 
          p.FULL_NAME, 
          t.STARTING_TOURNAMENT, 
          t.ENDING_TOURNAMENT  
      	FROM TEAMS t 
      	LEFT JOIN PLAYERS p ON t.PLAYER_ID = p.PLAYER_ID
      	WHERE t.TEAM_ID = :teamId
      ) as playerStandsForTeam,
      (
      	SELECT 
          pp.PLAYER_ID, 
          pp.TOURNAMENT_ID, 
          pp.FANTA_POINTS,
          fantaTournament.OWNER_ID
      	FROM PLAYERS_POINTS pp,
      	(
      		SELECT 
            ftt.TEAM_ID, 
            ft.STARTING_TOURNAMENT, 
            ft.ENDING_TOURNAMENT, 
            ft.TOURNAMENT_YEAR, 
            ft2.OWNER_ID
      		FROM FANTA_TOURNAMENTS_TEAMS ftt
      		LEFT JOIN FANTA_TOURNAMENTS ft ON ft.FANTA_TOURNAMENT_ID = ftt.FANTA_TOURNAMENT_ID
      		LEFT JOIN FANTA_TEAMS ft2 ON ft2.TEAM_ID = ftt.TEAM_ID
      		WHERE ftt.TEAM_ID = :teamId
      	) as fantaTournament
      	WHERE pp.TOURNAMENT_YEAR = fantaTournament.TOURNAMENT_YEAR 
      	AND pp.TOURNAMENT_ID >= fantaTournament.STARTING_TOURNAMENT 
      	AND pp.TOURNAMENT_ID <= fantaTournament.ENDING_TOURNAMENT 
      	AND pp.PLAYER_ID IN (
      		SELECT t.PLAYER_ID 
      		FROM TEAMS t 
      		WHERE t.TEAM_ID = :teamId
      	)
      	ORDER BY pp.PLAYER_ID ASC, pp.TOURNAMENT_ID ASC
      ) as pointPerPlayerByTournament
      WHERE pointPerPlayerByTournament.PLAYER_ID = playerStandsForTeam.PLAYER_ID
      AND
      (
      	IF (playerStandsForTeam.ENDING_TOURNAMENT IS NULL,
      		pointPerPlayerByTournament.TOURNAMENT_ID >= playerStandsForTeam.STARTING_TOURNAMENT,
      		pointPerPlayerByTournament.TOURNAMENT_ID BETWEEN playerStandsForTeam.STARTING_TOURNAMENT and playerStandsForTeam.ENDING_TOURNAMENT)
      )
      GROUP BY playerStandsForTeam.PLAYER_ID
      ORDER BY TOTAL_SCORE DESC;
    """.trimIndent()
  }
}