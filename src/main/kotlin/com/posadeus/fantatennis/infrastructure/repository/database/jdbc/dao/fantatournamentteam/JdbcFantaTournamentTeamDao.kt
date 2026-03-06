package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournamentteam

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentTeamDto.Companion.fantaTournamentTeamRowMapper
import com.posadeus.fantatennis.infrastructure.repository.exception.NoInsertException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcFantaTournamentTeamDao(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : FantaTournamentTeamDao {

  override fun persist(fantaTournamentTeam: JdbcFantaTournamentTeamDto) {

    val params = mapOf("teamId" to fantaTournamentTeam.teamId, "fantaTournamentId" to fantaTournamentTeam.fantaTournamentId)

    val rows = namedParameterJdbcTemplate.update(CREATE_FANTA_TOURNAMENTS_TEAMS_QUERY, params)

    if (rows == 0)
      throw NoInsertException("Insert failed on FANTA_TOURNAMENTS_TEAMS")
  }

  override fun retrieveBy(fantaTournamentId: Int): List<JdbcFantaTournamentTeamDto> =
      mapOf("fantaTournamentId" to fantaTournamentId)
          .let {
            namedParameterJdbcTemplate.query(RETRIEVE_FANTA_TOURNAMENTS_TEAMS_BY_TOURNAMENT_ID_QUERY, it, fantaTournamentTeamRowMapper)
          }

  override fun retrieveByTeamId(teamId: Int): JdbcFantaTournamentTeamDto =
      mapOf("teamId" to teamId)
          .let {
            namedParameterJdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENTS_TEAMS_BY_TEAM_ID_QUERY, it, fantaTournamentTeamRowMapper)
          }
      ?: throw EmptyResultDataAccessException(1)

  companion object {

    private val CREATE_FANTA_TOURNAMENTS_TEAMS_QUERY = """
      INSERT INTO FANTA_TOURNAMENTS_TEAMS
      (FANTA_TOURNAMENT_ID, TEAM_ID)
      VALUES(:fantaTournamentId, :teamId);
    """.trimIndent()

    private val RETRIEVE_FANTA_TOURNAMENTS_TEAMS_BY_TOURNAMENT_ID_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS_TEAMS
      WHERE FANTA_TOURNAMENT_ID = :fantaTournamentId
    """.trimIndent()

    private val RETRIEVE_FANTA_TOURNAMENTS_TEAMS_BY_TEAM_ID_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS_TEAMS
      WHERE TEAM_ID = :teamId
    """.trimIndent()
  }
}
