package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentResultsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrieveFantaTournamentResultsRepository.TournamentResultsDtoImpl
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

class JdbcRetrieveFantaTournamentResultsRepositoryTest {

  private val jdbcTemplate: JdbcTemplate = mockk()

  private val repository: RetrieveFantaTournamentResultsRepository = JdbcRetrieveFantaTournamentResultsRepository(jdbcTemplate)

  @Test
  fun `error on repository operation`() {

    val expected = ErrorFantaTournamentResults

    every { jdbcTemplate.query(RETRIEVE_QUERY, any<RowMapper<TournamentResultsDtoImpl>>()) } throws RuntimeException()

    assertThat(repository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `no results returned by the query`() {

    val expected = NotFoundFantaTournamentId

    every { jdbcTemplate.query(RETRIEVE_QUERY, any<RowMapper<TournamentResultsDtoImpl>>()) } returns emptyList()

    assertThat(repository.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `retrieve results successfully`() {

    val tournamentResultsDto1 = TournamentResultsDtoImpl(tournamentId = A_TOURNAMENT_ID,
                                                         teamId = A_TEAM_ID,
                                                         ownerId = AN_OWNER_ID,
                                                         playerId = A_PLAYER_ID,
                                                         playerFullName = A_PLAYER_FULL_NAME,
                                                         playerTotalScore = 2.00)
    val tournamentResultsDto2 = TournamentResultsDtoImpl(tournamentId = A_TOURNAMENT_ID,
                                                         teamId = A_TEAM_ID,
                                                         ownerId = AN_OWNER_ID,
                                                         playerId = ANOTHER_PLAYER_ID,
                                                         playerFullName = ANOTHER_PLAYER_FULL_NAME,
                                                         playerTotalScore = 1.00)
    val tournamentResultsDto3 = TournamentResultsDtoImpl(tournamentId = A_TOURNAMENT_ID,
                                                         teamId = ANOTHER_TEAM_ID,
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

    every { jdbcTemplate.query(RETRIEVE_QUERY, any<RowMapper<TournamentResultsDtoImpl>>()) } returns tournamentResultsDto

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
      SELECT ft.FANTA_TOURNAMENT_ID, ft2.TEAM_ID, p.FULL_NAME, p.PLAYER_ID, pps.TOTAL_SCORE, ft2.OWNER_ID 
      FROM fanta_tennis.FANTA_TOURNAMENTS ft 
      	LEFT JOIN fanta_tennis.FANTA_TOURNAMENTS_TEAMS ftt ON ft.FANTA_TOURNAMENT_ID = ftt.FANTA_TOURNAMENT_ID 
      	LEFT JOIN fanta_tennis.FANTA_TEAMS ft2 ON ftt.TEAM_ID = ft2.TEAM_ID
      	LEFT JOIN fanta_tennis.TEAMS t ON ft2.TEAM_ID = t.TEAM_ID
      	LEFT JOIN fanta_tennis.PLAYERS p ON t.PLAYER_ID = p.PLAYER_ID
      	LEFT JOIN (	
      		SELECT 
      		  pp.PLAYER_ID AS ID,
      		  SUM(pp.FANTA_POINTS) AS TOTAL_SCORE
      		FROM 
      		  fanta_tennis.PLAYERS_POINTS pp,
      		  fanta_tennis.TEAMS t2,
      		  (
      			  SELECT *
      			  FROM fanta_tennis.FANTA_TOURNAMENTS ft3 
      			  WHERE ft3.FANTA_TOURNAMENT_ID = 3
      		  ) AS fttt
      		WHERE 
      		  pp.PLAYER_ID = t2.PLAYER_ID 
      		  AND pp.TOURNAMENT_YEAR = fttt.TOURNAMENT_YEAR
      		  AND t2.STARTING_TOURNAMENT >= fttt.STARTING_TOURNAMENT
      		  AND pp.TOURNAMENT_ID >= t2.STARTING_TOURNAMENT 
      		  AND (t2.ENDING_TOURNAMENT IS NULL OR pp.TOURNAMENT_ID <= t2.ENDING_TOURNAMENT) 
      		  GROUP BY pp.PLAYER_ID
      	) AS pps ON pps.ID = p.PLAYER_ID 
      WHERE ft.FANTA_TOURNAMENT_ID = 3
      ORDER BY t.TEAM_ID, pps.TOTAL_SCORE DESC;
    """.trimIndent()
  }
}