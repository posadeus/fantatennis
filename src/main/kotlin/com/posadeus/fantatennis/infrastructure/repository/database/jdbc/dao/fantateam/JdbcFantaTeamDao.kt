package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantateam

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTeamId
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto.Companion.fantaTeamRowMapper
import com.posadeus.fantatennis.infrastructure.repository.exception.NoInsertException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder

class JdbcFantaTeamDao(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : FantaTeamDao {

  override fun retrieveBy(teamId: Int): JdbcFantaTeamDto =
      namedParameterJdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, mapOf("teamId" to teamId), fantaTeamRowMapper)
      ?: throw EmptyResultDataAccessException("No fanta team found with id $teamId", 1)

  override fun persist(ownerId: String): FantaTeamId {

    val keyHolder = GeneratedKeyHolder()
    val rows = namedParameterJdbcTemplate.update(CREATE_FANTA_TEAMS_QUERY,
                                                 MapSqlParameterSource().addValue("ownerId", ownerId),
                                                 keyHolder,
                                                 arrayOf("TEAM_ID"))

    if (rows == 0 || keyHolder.key == null)
      throw NoInsertException("Insert failed or no key generated on FANTA_TEAMS")

    return keyHolder.key!!.toInt()
  }

  companion object {

    private val RETRIEVE_FANTA_TEAM_QUERY = """
      SELECT *
      FROM FANTA_TEAMS
      WHERE TEAM_ID = :teamId;
    """.trimIndent()

    private val CREATE_FANTA_TEAMS_QUERY = """
      INSERT INTO FANTA_TEAMS
      (OWNER_ID)
      VALUES(:ownerId);
    """.trimIndent()
  }
}
