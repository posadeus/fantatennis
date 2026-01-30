package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournament

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto.Companion.fantaTournamentRowMapper
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcFantaTournamentDao(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : FantaTournamentDao {

  override fun retrieveBy(fantaTournamentId: Int): JdbcFantaTournamentDto =
      namedParameterJdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_QUERY, mapOf("id" to fantaTournamentId), fantaTournamentRowMapper)
      ?: throw EmptyResultDataAccessException("No fanta tournament found with id $fantaTournamentId", 1)

  companion object {

    private val RETRIEVE_FANTA_TOURNAMENT_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS 
      WHERE FANTA_TOURNAMENT_ID = :id;
    """.trimIndent()
  }
}
