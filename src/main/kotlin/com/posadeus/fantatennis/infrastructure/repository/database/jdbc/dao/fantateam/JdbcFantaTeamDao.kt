package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantateam

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto.Companion.fantaTeamRowMapper
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcFantaTeamDao(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : FantaTeamDao {

  override fun retrieveBy(teamId: Int): JdbcFantaTeamDto =
      namedParameterJdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, mapOf("teamId" to teamId), fantaTeamRowMapper)
      ?: throw EmptyResultDataAccessException("No fanta team found with id $teamId", 1)

  companion object {

    private val RETRIEVE_FANTA_TEAM_QUERY = """
      SELECT *
      FROM FANTA_TEAMS
      WHERE TEAM_ID = :teamId;
    """.trimIndent()
  }
}
