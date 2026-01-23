package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto.Companion.tournamentRowMapper
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcTournamentDao(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : TournamentDao {

  override fun retrieveAllBy(year: Int): List<JdbcTournamentDto> =
      namedParameterJdbcTemplate.query(RETRIEVE_TOURNAMENTS_QUERY, mapOf("year" to year), tournamentRowMapper)

  override fun retrieveBy(id: Int): JdbcTournamentDto =
      namedParameterJdbcTemplate.queryForObject(RETRIEVE_TOURNAMENT_QUERY, mapOf("id" to id), tournamentRowMapper)
      ?: throw EmptyResultDataAccessException("No tournament found with id $id", 1)

  companion object {

    private val RETRIEVE_TOURNAMENTS_QUERY = """
      SELECT *
      FROM TOURNAMENTS
      WHERE `YEAR` = :year;
    """.trimIndent()

    private val RETRIEVE_TOURNAMENT_QUERY = """
      SELECT *
      FROM TOURNAMENTS
      WHERE TOURNAMENT_ID = :id;
    """.trimIndent()
  }
}
