package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentsTeamsDto.Companion.fantaTournamentsTeamsRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto.Companion.teamRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamPlayerPointsDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamPlayerPointsDto.Companion.teamPlayerPointsRowMapper
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcRetrieveFantaTeamRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) : RetrieveFantaTeamRepository {

  override fun retrieve(teamId: Int): Team {

    try {

      val fantaTournamentsTeamsDto = jdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_BY_TEAM_QUERY,
                                                                 mapOf("id" to teamId),
                                                                 fantaTournamentsTeamsRowMapper)
      val teamsDto = jdbcTemplate.query(RETRIEVE_TEAMS_QUERY, mapOf("id" to teamId), teamRowMapper)

      if (teamsDto.isEmpty())
        return FoundTeam(team = TeamDto(owner = fantaTournamentsTeamsDto!!.ownerId, players = emptyList(), totalScore = 0.00))

      val inputParams = mapOf("teamId" to fantaTournamentsTeamsDto!!.teamId,
                              "startingTournamentId" to fantaTournamentsTeamsDto.startingTournamentId,
                              "endingTournamentId" to fantaTournamentsTeamsDto.endingTournamentId,
                              "players" to teamsDto.map { it.playerId }.toSet(),
                              "tournamentYear" to fantaTournamentsTeamsDto.tournamentYear)
      val teamPlayerPointsDto = jdbcTemplate.query(RETRIEVE_PLAYER_POINTS_QUERY, inputParams, teamPlayerPointsRowMapper)

      val playerScores = teamPlayerPointsDto
          .groupBy { it.playerId }
          .entries
          .map { entry ->
            val teamPlayerRows = teamsDto.filter { it.playerId == entry.key }
            TempPlayerPoints(playerId = entry.key,
                             playerName = entry.value.first().playerName,
                             totalPoints = toPlayerTotalPoints(teamPlayerRows, entry.value, fantaTournamentsTeamsDto.endingTournamentId))
          }

      return playerScores
          .let { instance ->
            TeamDto(owner = fantaTournamentsTeamsDto.ownerId,
                    players = instance
                        .map { TeamPlayerDto(fullName = it.playerName, fantaPoints = it.totalPoints) }
                        .sortedByDescending { it.fantaPoints },
                    totalScore = instance.sumOf { it.totalPoints })
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

  private fun toPlayerTotalPoints(teamPlayerRows: List<JdbcTeamDto>,
                                  teamPlayerPoints: List<JdbcTeamPlayerPointsDto>,
                                  defaultEndingTournament: Int) =
      teamPlayerPoints
          .filter { tpp ->
            teamPlayerRows.any { tpp.tournamentId in it.startingTournamentId .. (it.endingTournamentId ?: defaultEndingTournament) }
          }
          .sumOf { it.tournamentScore }

  private data class TempPlayerPoints(val playerId: String,
                                      val playerName: String,
                                      val totalPoints: Double)

  companion object {

    // TODO Use this query instead of all the others and remove logic to calculate total points
    private val RETRIEVE_QUERY = """
      SELECT 
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
      	WHERE t.TEAM_ID = 26
      ) as playerStandsForTeam,
      (
      	SELECT 
          pp.PLAYER_ID, 
          pp.TOURNAMENT_ID, 
          pp.FANTA_POINTS
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
      		WHERE ftt.TEAM_ID = 26
      	) as fantaTournament
      	WHERE pp.TOURNAMENT_YEAR = fantaTournament.TOURNAMENT_YEAR 
      	AND pp.TOURNAMENT_ID >= fantaTournament.STARTING_TOURNAMENT 
      	AND pp.TOURNAMENT_ID <= fantaTournament.ENDING_TOURNAMENT 
      	AND pp.PLAYER_ID IN (
      		SELECT t.PLAYER_ID 
      		FROM TEAMS t 
      		WHERE t.TEAM_ID = 26
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

    private val RETRIEVE_FANTA_TOURNAMENT_BY_TEAM_QUERY = """
      SELECT ftt.TEAM_ID, ft.STARTING_TOURNAMENT, ft.ENDING_TOURNAMENT, ft.TOURNAMENT_YEAR, ft2.OWNER_ID
      FROM FANTA_TOURNAMENTS_TEAMS ftt
      LEFT JOIN FANTA_TOURNAMENTS ft ON ft.FANTA_TOURNAMENT_ID = ftt.FANTA_TOURNAMENT_ID
      LEFT JOIN FANTA_TEAMS ft2 ON ft2.TEAM_ID = ftt.TEAM_ID
      WHERE ftt.TEAM_ID = :id;
    """.trimIndent()

    private val RETRIEVE_TEAMS_QUERY = """
      SELECT t.TEAM_ID, t.PLAYER_ID, t.STARTING_TOURNAMENT, t.ENDING_TOURNAMENT  
      FROM TEAMS t 
      WHERE t.TEAM_ID = :id
    """.trimIndent()

    private val RETRIEVE_PLAYER_POINTS_QUERY = """
      SELECT pp.PLAYER_ID, p.FULL_NAME, pp.TOURNAMENT_ID, pp.FANTA_POINTS
      FROM PLAYERS_POINTS pp  
      LEFT JOIN PLAYERS p ON p.PLAYER_ID = pp.PLAYER_ID
      WHERE pp.TOURNAMENT_YEAR = :tournamentYear
      AND pp.TOURNAMENT_ID >= :startingTournamentId
      AND pp.TOURNAMENT_ID <= :endingTournamentId
      AND pp.PLAYER_ID IN (:players)
      ORDER BY pp.PLAYER_ID ASC, pp.TOURNAMENT_ID ASC;
    """.trimIndent()
  }
}