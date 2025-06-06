package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsTeamsDto
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TeamPlayerPointsDto
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcRetrieveFantaTeamRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) : RetrieveFantaTeamRepository {

  override fun retrieve(teamId: Int): Team {

    try {

      val fantaTournamentsTeamsDto = jdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_BY_TEAM_QUERY,
                                                                 mapOf("id" to teamId),
                                                                 fantaTournamentTeamRowMapper)

      val inputParams = mapOf("teamId" to fantaTournamentsTeamsDto!!.teamId,
                              "startingTournamentId" to fantaTournamentsTeamsDto.startingTournamentId,
                              "endingTournamentId" to fantaTournamentsTeamsDto.endingTournamentId,
                              "tournamentYear" to fantaTournamentsTeamsDto.tournamentYear)
      val teamPlayerPointsDto = jdbcTemplate.query(RETRIEVE_PLAYER_POINTS_QUERY, inputParams, pointsRowMapper)

      return teamPlayerPointsDto
          .let {
            TeamDto(owner = fantaTournamentsTeamsDto.ownerId,
                    players = it.map { TeamPlayerDto(fullName = it.playerName, fantaPoints = it.totalScore) },
                    totalScore = it.sumOf { it.totalScore })
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

  private val fantaTournamentTeamRowMapper = RowMapper { rs, _ ->
    FantaTournamentsTeamsDto(teamId = rs.getInt("TEAM_ID"),
                             startingTournamentId = rs.getInt("STARTING_TOURNAMENT"),
                             endingTournamentId = rs.getInt("ENDING_TOURNAMENT"),
                             tournamentYear = rs.getInt("TOURNAMENT_YEAR"),
                             ownerId = rs.getString("OWNER_ID"))
  }

  private val pointsRowMapper = RowMapper { rs, _ ->
    TeamPlayerPointsDto(playerId = rs.getString("PLAYER_ID"),
                        playerName = rs.getString("FULL_NAME"),
                        totalScore = rs.getDouble("TOTAL_POINTS"))
  }

  companion object {

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