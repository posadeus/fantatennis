package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentResultsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentResultsDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcRetrieveFantaTournamentResultsRepositoryTest {

  private val jdbcTemplate: NamedParameterJdbcTemplate = mockk()

  private val repository: RetrieveFantaTournamentResultsRepository = JdbcRetrieveFantaTournamentResultsRepository(jdbcTemplate)

  @Test
  fun `error on repository operation`() {

    val params = mapOf("fantaTournamentId" to A_TOURNAMENT_ID)
    val expected = ErrorFantaTournamentResults

    every { jdbcTemplate.query(RETRIEVE_QUERY, params, any<RowMapper<JdbcTournamentResultsDto>>()) } throws RuntimeException()

    assertThat(repository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `no results returned by the query`() {

    val params = mapOf("fantaTournamentId" to A_TOURNAMENT_ID)
    val expected = NotFoundFantaTournamentId

    every { jdbcTemplate.query(RETRIEVE_QUERY, params, any<RowMapper<JdbcTournamentResultsDto>>()) } returns emptyList()

    assertThat(repository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `retrieve results successfully`() {

    val params = mapOf("fantaTournamentId" to A_TOURNAMENT_ID)

    val tournamentResultsDto1 = JdbcTournamentResultsDto(teamId = A_TEAM_ID,
                                                         ownerId = AN_OWNER_ID,
                                                         playerId = A_PLAYER_ID,
                                                         playerFullName = A_PLAYER_FULL_NAME,
                                                         playerTotalScore = 2.00)
    val tournamentResultsDto2 = JdbcTournamentResultsDto(teamId = A_TEAM_ID,
                                                         ownerId = AN_OWNER_ID,
                                                         playerId = ANOTHER_PLAYER_ID,
                                                         playerFullName = ANOTHER_PLAYER_FULL_NAME,
                                                         playerTotalScore = 1.00)
    val tournamentResultsDto3 = JdbcTournamentResultsDto(teamId = ANOTHER_TEAM_ID,
                                                         ownerId = ANOTHER_OWNER_ID,
                                                         playerId = A_THIRD_PLAYER_ID,
                                                         playerFullName = A_THIRD_PLAYER_FULL_NAME,
                                                         playerTotalScore = 4.00)
    val tournamentResultsDto = listOf(tournamentResultsDto1, tournamentResultsDto2, tournamentResultsDto3)

    val aPlayer = TeamPlayerDto(fullName = A_PLAYER_FULL_NAME, fantaPoints = 2.00)
    val anotherPlayer = TeamPlayerDto(fullName = ANOTHER_PLAYER_FULL_NAME, fantaPoints = 1.00)
    val aTeam = TeamDto(owner = AN_OWNER_ID, players = listOf(aPlayer, anotherPlayer), totalScore = 3.00)
    val aThirdPlayer = TeamPlayerDto(fullName = A_THIRD_PLAYER_FULL_NAME, fantaPoints = 4.00)
    val anotherTeam = TeamDto(owner = ANOTHER_OWNER_ID, players = listOf(aThirdPlayer), totalScore = 4.00)
    val expected = FoundFantaTournamentResults(TournamentDto(teams = listOf(anotherTeam, aTeam)))

    every { jdbcTemplate.query(RETRIEVE_QUERY, params, any<RowMapper<JdbcTournamentResultsDto>>()) } returns tournamentResultsDto

    assertThat(repository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1
    private const val A_TEAM_ID = 3
    private const val ANOTHER_TEAM_ID = 4
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val A_THIRD_PLAYER_ID = "A_THIRD_PLAYER_ID"
    private const val A_PLAYER_FULL_NAME = "A_PLAYER_FULL_NAME"
    private const val ANOTHER_PLAYER_FULL_NAME = "ANOTHER_PLAYER_FULL_NAME"
    private const val A_THIRD_PLAYER_FULL_NAME = "A_THIRD_PLAYER_FULL_NAME"
    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val ANOTHER_OWNER_ID = "ANOTHER_OWNER_ID"

    private val RETRIEVE_QUERY = """
      SELECT 
        playerStandsForTeam.TEAM_ID, 
        ft2.OWNER_ID,
        playerStandsForTeam.PLAYER_ID, 
        playerPointsByTournament.FULL_NAME, 
        SUM(playerPointsByTournament.FANTA_POINTS) AS TOTAL_SCORE 
      FROM 
      (
        SELECT 
          pp.PLAYER_ID, 
          pl.FULL_NAME, 
          pp.TOURNAMENT_ID, 
          pp.FANTA_POINTS
        FROM PLAYERS_POINTS pp
        LEFT JOIN PLAYERS pl ON pl.PLAYER_ID = pp.PLAYER_ID,
        (
          SELECT 
            ft.STARTING_TOURNAMENT, 
            ft.ENDING_TOURNAMENT, 
            ft.TOURNAMENT_YEAR
          FROM FANTA_TOURNAMENTS ft 
          WHERE ft.FANTA_TOURNAMENT_ID = :fantaTournamentId
        ) AS fttt
        WHERE pp.TOURNAMENT_YEAR = fttt.TOURNAMENT_YEAR 
        AND pp.TOURNAMENT_ID >= fttt.STARTING_TOURNAMENT 
        AND pp.TOURNAMENT_ID <= fttt.ENDING_TOURNAMENT 
        ORDER BY 
          pp.PLAYER_ID ASC, 
          pp.TOURNAMENT_ID ASC
      ) as playerPointsByTournament,
      (
        SELECT 
          tm.TEAM_ID, 
          tm.PLAYER_ID, 
          tm.STARTING_TOURNAMENT, 
          tm.ENDING_TOURNAMENT  
        FROM TEAMS tm 
        WHERE tm.TEAM_ID IN (
          SELECT ftts.TEAM_ID
          FROM FANTA_TOURNAMENTS_TEAMS as ftts
          WHERE ftts.FANTA_TOURNAMENT_ID = :fantaTournamentId
        ) ORDER BY tm.TEAM_ID
      ) as playerStandsForTeam
      LEFT JOIN FANTA_TEAMS ft2 ON playerStandsForTeam.TEAM_ID = ft2.TEAM_ID
      WHERE playerStandsForTeam.PLAYER_ID = playerPointsByTournament.PLAYER_ID 
      AND 
      (
        IF (playerStandsForTeam.ENDING_TOURNAMENT IS NULL,
          playerPointsByTournament.TOURNAMENT_ID >= playerStandsForTeam.STARTING_TOURNAMENT,
          playerPointsByTournament.TOURNAMENT_ID BETWEEN playerStandsForTeam.STARTING_TOURNAMENT and playerStandsForTeam.ENDING_TOURNAMENT)
      )
      GROUP BY 
        playerStandsForTeam.PLAYER_ID, 
        playerStandsForTeam.TEAM_ID
      ORDER BY 
        TEAM_ID ASC, 
        TOTAL_SCORE DESC;
    """.trimIndent()
  }
}