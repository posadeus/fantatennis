package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto.Companion.fantaTeamRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamResultsDto.Companion.fantaTeamResultsRowMapper
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcRetrieveFantaTeamRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) : RetrieveFantaTeamRepository {

  override fun retrieve(teamId: Int): Team {

    try {

      val params = mapOf("teamId" to teamId)

      val fantaTeamDto = jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, params, fantaTeamRowMapper)

      val fantaTeamResultsDto = jdbcTemplate.query(RETRIEVE_FANTA_TEAM_RESULTS_QUERY, params, fantaTeamResultsRowMapper)

      return fantaTeamResultsDto
          .let { instance ->
            TeamDto(owner = fantaTeamDto!!.ownerId,
                    players = instance
                        .map { PlayerPointsDto(fullName = it.playerFullName, fantaPoints = it.playerTotalScore) }
                        .sortedByDescending { it.fantaPoints },
                    totalScore = instance.sumOf { it.playerTotalScore })
          }
          .let { FoundTeam(it) }
    }
    catch (e: EmptyResultDataAccessException) {

      return TeamIdNotFoundTeam
    }
    catch (e: Exception) {

      return ErrorTeam
    }
  }

  companion object {

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