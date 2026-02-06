package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournament

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto.Companion.fantaTournamentRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.NewJdbcFantaTournamentDto
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcFantaTournamentDao(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : FantaTournamentDao {

  override fun retrieveBy(fantaTournamentId: Int): JdbcFantaTournamentDto =
      namedParameterJdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_QUERY, mapOf("id" to fantaTournamentId), fantaTournamentRowMapper)
      ?: throw EmptyResultDataAccessException("No fanta tournament found with id $fantaTournamentId", 1)

  override fun retrieveAll(): List<JdbcFantaTournamentDto> =
      namedParameterJdbcTemplate.query(RETRIEVE_ALL_FANTA_TOURNAMENT_QUERY, fantaTournamentRowMapper)

  override fun persist(fantaTournamentDto: NewJdbcFantaTournamentDto): Int =
      fantaTournamentDto
          .let {
            mapOf("startingTournamentId" to it.startingTournamentId,
                  "endingTournamentId" to it.endingTournamentId,
                  "year" to it.year)
          }
          .let { namedParameterJdbcTemplate.update(CREATE_FANTA_TOURNAMENT_QUERY, it) }

  companion object {

    private val RETRIEVE_FANTA_TOURNAMENT_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS 
      WHERE FANTA_TOURNAMENT_ID = :id;
    """.trimIndent()

    private val RETRIEVE_ALL_FANTA_TOURNAMENT_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS; 
    """.trimIndent()

    private val CREATE_FANTA_TOURNAMENT_QUERY = """
      INSERT INTO FANTA_TOURNAMENTS (STARTING_TOURNAMENT, ENDING_TOURNAMENT, TOURNAMENT_YEAR)
      VALUES(:startingTournamentId, :endingTournamentId, :year);
    """.trimIndent()
  }
}
