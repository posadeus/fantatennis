package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.team

import com.posadeus.fantatennis.domain.exception.InvalidAddPlayersException
import com.posadeus.fantatennis.domain.model.TeamId
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.DataBaseErrorManager.manageError
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto.Companion.teamRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcTeamDao(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : TeamDao {

  override fun persist(teams: Set<JdbcTeamDto>) {

    try {

      val batchUpdateResult = namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, teams.map(::toQueryParams).toTypedArray())

      if (batchUpdateResult.any { it != 1 }) {

          throw InvalidAddPlayersException(error = "Players [${manageError(teams, batchUpdateResult) { it.playerId }}] not inserted.")
      }
    }
    catch (e: RuntimeException) {

      throw InvalidAddPlayersException(error = "${e.message}")
    }
  }

  override fun retrieveBy(teamIds: Set<TeamId>): List<JdbcTeamDto> =
      namedParameterJdbcTemplate.query(RETRIEVE_TEAMS_QUERY, mapOf("teamIds" to teamIds), teamRowMapper)

  private fun toQueryParams(team: JdbcTeamDto): Map<String, Any> =
      mapOf("teamId" to team.teamId, "playerId" to team.playerId, "startingTournamentId" to team.startingTournamentId)

  companion object {

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
