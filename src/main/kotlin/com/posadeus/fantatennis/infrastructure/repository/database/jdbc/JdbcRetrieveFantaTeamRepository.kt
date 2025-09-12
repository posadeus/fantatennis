package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.*
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcRetrieveFantaTeamRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) : RetrieveFantaTeamRepository {

  override fun retrieve(teamId: Int): Team {

    try {

      val fantaTournamentsTeamsDto = jdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_BY_TEAM_QUERY,
                                                                 mapOf("id" to teamId),
                                                                 fantaTournamentTeamRowMapper)
      val teamsDto = jdbcTemplate.query(RETRIEVE_TEAMS_QUERY, mapOf("id" to teamId), teamRowMapper)

      if (teamsDto.isEmpty())
        return FoundTeam(team = TeamDto(owner = fantaTournamentsTeamsDto!!.ownerId, players = emptyList(), totalScore = 0.00))

      val inputParams = mapOf("teamId" to fantaTournamentsTeamsDto!!.teamId,
                              "startingTournamentId" to fantaTournamentsTeamsDto.startingTournamentId,
                              "endingTournamentId" to fantaTournamentsTeamsDto.endingTournamentId,
                              "players" to teamsDto.map { it.playerId }.toSet(),
                              "tournamentYear" to fantaTournamentsTeamsDto.tournamentYear)
      val teamPlayerPointsDto = jdbcTemplate.query(RETRIEVE_PLAYER_POINTS_QUERY, inputParams, pointsRowMapper)

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

  private val fantaTournamentTeamRowMapper = RowMapper { rs, _ ->
    JdbcFantaTournamentsTeamsDto(teamId = rs.getInt("TEAM_ID"),
                                 startingTournamentId = rs.getInt("STARTING_TOURNAMENT"),
                                 endingTournamentId = rs.getInt("ENDING_TOURNAMENT"),
                                 tournamentYear = rs.getInt("TOURNAMENT_YEAR"),
                                 ownerId = rs.getString("OWNER_ID"))
  }

  private val teamRowMapper = RowMapper { rs, _ ->
    JdbcTeamDto(teamId = rs.getInt("TEAM_ID"),
                playerId = rs.getString("PLAYER_ID"),
                startingTournamentId = rs.getInt("STARTING_TOURNAMENT"),
                endingTournamentId = rs.getObject("ENDING_TOURNAMENT", Integer::class.java)?.toInt())
  }

  private val pointsRowMapper = RowMapper { rs, _ ->
    JdbcTeamPlayerPointsDto(playerId = rs.getString("PLAYER_ID"),
                            playerName = rs.getString("FULL_NAME"),
                            tournamentId = rs.getInt("TOURNAMENT_ID"),
                            tournamentScore = rs.getDouble("FANTA_POINTS"))
  }

  private data class TempPlayerPoints(val playerId: String,
                                      val playerName: String,
                                      val totalPoints: Double)

  companion object {

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