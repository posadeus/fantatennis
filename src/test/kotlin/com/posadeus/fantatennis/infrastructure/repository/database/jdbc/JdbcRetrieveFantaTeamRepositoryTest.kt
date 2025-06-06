package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsTeamsDto
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TeamPlayerPointsDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcRetrieveFantaTeamRepositoryTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val repository: RetrieveFantaTeamRepository = JdbcRetrieveFantaTeamRepository(jdbcTemplate)

  @Test
  fun `retrieve fanta team fails due to missing team or tournament`() {

    val teamParams = mapOf("id" to A_TEAM_ID)

    val expected = TeamIdNotFoundTeam

    every {
      jdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_BY_TEAM_QUERY, teamParams, any<RowMapper<FantaTournamentsTeamsDto>>())
    } throws EmptyResultDataAccessException(1)

    assertThat(repository.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `retrieve fanta team fails due to db error`() {

    val teamParams = mapOf("id" to A_TEAM_ID)
    val fantaTournamentsTeamsDto = FantaTournamentsTeamsDto(teamId = A_TEAM_ID,
                                                            startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                            endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                            tournamentYear = A_TOURNAMENT_YEAR,
                                                            ownerId = AN_OWNER_ID)
    val pointsParams = mapOf("tournamentYear" to A_TOURNAMENT_YEAR,
                             "startingTournamentId" to A_STARTING_TOURNAMENT_ID,
                             "endingTournamentId" to AN_ENDING_TOURNAMENT_ID,
                             "teamId" to A_TEAM_ID)

    val expected = ErrorTeam

    every {
      jdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_BY_TEAM_QUERY, teamParams, any<RowMapper<FantaTournamentsTeamsDto>>())
    } returns fantaTournamentsTeamsDto
    every {
      jdbcTemplate.query(RETRIEVE_PLAYER_POINTS_QUERY, pointsParams, any<RowMapper<TeamPlayerPointsDto>>())
    } throws RuntimeException("Scary error!")

    assertThat(repository.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `retrieve fanta team succeeded but the team as no players`() {

    val teamParams = mapOf("id" to A_TEAM_ID)
    val fantaTournamentsTeamsDto = FantaTournamentsTeamsDto(teamId = A_TEAM_ID,
                                                            startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                            endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                            tournamentYear = A_TOURNAMENT_YEAR,
                                                            ownerId = AN_OWNER_ID)
    val pointsParams = mapOf("tournamentYear" to A_TOURNAMENT_YEAR,
                             "startingTournamentId" to A_STARTING_TOURNAMENT_ID,
                             "endingTournamentId" to AN_ENDING_TOURNAMENT_ID,
                             "teamId" to A_TEAM_ID)

    val expected = FoundTeam(team = TeamDto(owner = AN_OWNER_ID, players = emptyList(), totalScore = 0.00))

    every {
      jdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_BY_TEAM_QUERY, teamParams, any<RowMapper<FantaTournamentsTeamsDto>>())
    } returns fantaTournamentsTeamsDto
    every { jdbcTemplate.query(RETRIEVE_PLAYER_POINTS_QUERY, pointsParams, any<RowMapper<TeamPlayerPointsDto>>()) } returns emptyList()

    assertThat(repository.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `retrieve fanta team succeeded`() {

    val teamParams = mapOf("id" to A_TEAM_ID)
    val fantaTournamentsTeamsDto = FantaTournamentsTeamsDto(teamId = A_TEAM_ID,
                                                            startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                                            endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                                            tournamentYear = A_TOURNAMENT_YEAR,
                                                            ownerId = AN_OWNER_ID)
    val pointsParams = mapOf("tournamentYear" to A_TOURNAMENT_YEAR,
                             "startingTournamentId" to A_STARTING_TOURNAMENT_ID,
                             "endingTournamentId" to AN_ENDING_TOURNAMENT_ID,
                             "teamId" to A_TEAM_ID)
    val teamPlayerPointsDto = listOf(TeamPlayerPointsDto(playerId = A_PLAYER_ID, playerName = A_PLAYER_NAME, totalScore = 12.00),
                                     TeamPlayerPointsDto(playerId = ANOTHER_PLAYER_ID, playerName = ANOTHER_PLAYER_NAME, totalScore = 1.00),
                                     TeamPlayerPointsDto(playerId = A_THIRD_PLAYER_ID, playerName = A_THIRD_PLAYER_NAME, totalScore = 7.00))

    val expected = FoundTeam(team = TeamDto(owner = AN_OWNER_ID,
                                            players = listOf(TeamPlayerDto(fullName = A_PLAYER_NAME, fantaPoints = 12.00),
                                                             TeamPlayerDto(fullName = ANOTHER_PLAYER_NAME, fantaPoints = 1.00),
                                                             TeamPlayerDto(fullName = A_THIRD_PLAYER_NAME, fantaPoints = 7.00)),
                                            totalScore = 20.00))

    every {
      jdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_BY_TEAM_QUERY, teamParams, any<RowMapper<FantaTournamentsTeamsDto>>())
    } returns fantaTournamentsTeamsDto
    every { jdbcTemplate.query(RETRIEVE_PLAYER_POINTS_QUERY, pointsParams, any<RowMapper<TeamPlayerPointsDto>>()) } returns teamPlayerPointsDto

    assertThat(repository.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TEAM_ID = 1234
    private const val A_STARTING_TOURNAMENT_ID = 1
    private const val AN_ENDING_TOURNAMENT_ID = 2
    private const val A_TOURNAMENT_YEAR = 2000
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val A_THIRD_PLAYER_ID = "A_THIRD_PLAYER_ID"
    private const val A_PLAYER_NAME = "A_PLAYER_NAME"
    private const val ANOTHER_PLAYER_NAME = "ANOTHER_PLAYER_NAME"
    private const val A_THIRD_PLAYER_NAME = "A_THIRD_PLAYER_NAME"
    private const val AN_OWNER_ID = "AN_OWNER_ID"

    private val RETRIEVE_FANTA_TOURNAMENT_BY_TEAM_QUERY = """
      SELECT ftt.TEAM_ID, ft.STARTING_TOURNAMENT, ft.ENDING_TOURNAMENT, ft.TOURNAMENT_YEAR, ft2.OWNER_ID
      FROM FANTA_TOURNAMENTS_TEAMS ftt
      LEFT JOIN FANTA_TOURNAMENTS ft ON ft.FANTA_TOURNAMENT_ID = ftt.FANTA_TOURNAMENT_ID
      LEFT JOIN FANTA_TEAMS ft2 ON ft2.TEAM_ID = ftt.TEAM_ID
      WHERE ftt.TEAM_ID = :id;
    """.trimIndent()

    private val RETRIEVE_PLAYER_POINTS_QUERY = """
      SELECT pp.PLAYER_ID, p.FULL_NAME, SUM(pp.FANTA_POINTS) AS TOTAL_POINTS
      FROM PLAYERS_POINTS pp 
      LEFT JOIN PLAYERS p ON p.PLAYER_ID = pp.PLAYER_ID
      WHERE pp.TOURNAMENT_YEAR = :tournamentYear
      AND pp.TOURNAMENT_ID BETWEEN :startingTournamentId AND :endingTournamentId
      AND pp.PLAYER_ID IN (
        SELECT t.PLAYER_ID
        FROM TEAMS t
        WHERE t.TEAM_ID = :teamId
      )
      GROUP BY pp.PLAYER_ID
      ORDER BY TOTAL_POINTS DESC;
    """.trimIndent()
  }
}