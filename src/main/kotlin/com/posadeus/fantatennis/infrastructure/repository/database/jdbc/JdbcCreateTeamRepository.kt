package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.CreateTeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.exception.NoInsertException
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.transaction.annotation.Transactional

class JdbcCreateTeamRepository(private val jdbcTemplate: JdbcTemplate,
                               private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : CreateTeamRepository {

                                 // TODO refactor
  @Transactional
  override fun create(ownerId: String, fantaTournamentId: Int): FantaTeam =
      try {

        namedParameterJdbcTemplate.queryForObject(RETRIEVE_FANTA_TOURNAMENT_QUERY, mapOf("id" to fantaTournamentId), rowMapper)
        createTeam(ownerId, fantaTournamentId)
      }
      catch (e: EmptyResultDataAccessException) {

        LOGGER.error("Fanta Tournament $fantaTournamentId not found")
        FantaTeamError
      }
      catch (e: Exception) {

        LOGGER.error("Error during DB operation ${e.message}")
        FantaTeamError
      }

  private val rowMapper = RowMapper { rs, _ ->
    JdbcFantaTournamentDto(id = rs.getInt("FANTA_TOURNAMENT_ID"),
                           startingTournamentId = rs.getInt("STARTING_TOURNAMENT"),
                           endingTournamentId = rs.getInt("ENDING_TOURNAMENT"),
                           year = rs.getInt("TOURNAMENT_YEAR"))
  }

  private fun createTeam(ownerId: String, fantaTournamentId: Int): FantaTeam =
      try {

        val keyHolder = GeneratedKeyHolder()
        val sqlParameterSource = MapSqlParameterSource().addValue("ownerId", ownerId)
        val rows = namedParameterJdbcTemplate.update(CREATE_QUERY_FANTA_TEAMS, sqlParameterSource, keyHolder)

        if (rows == 0 || keyHolder.key == null)
          throw NoInsertException("Insert failed or no key generated on FANTA_TEAMS")

        val fantaTeamId = keyHolder.key!!.toInt()

        jdbcTemplate.update(CREATE_QUERY_FANTA_TOURNAMENTS_TEAMS, fantaTournamentId, fantaTeamId)

        FantaTeamOk(id = fantaTeamId, ownerId = ownerId)
      }
      catch (e: RuntimeException) {

        LOGGER.error("Error during DB operation ${e.message}")
        FantaTeamError
      }

  companion object {

    private val LOGGER = LoggerFactory.getLogger(JdbcCreateTeamRepository::class.java)

    private val RETRIEVE_FANTA_TOURNAMENT_QUERY = """
      SELECT *
      FROM FANTA_TOURNAMENTS 
      WHERE FANTA_TOURNAMENT_ID = :id;
    """.trimIndent()

    private val CREATE_QUERY_FANTA_TEAMS = """
      INSERT INTO FANTA_TEAMS
      (OWNER_ID)
      VALUES(:ownerId);
    """.trimIndent()

    private val CREATE_QUERY_FANTA_TOURNAMENTS_TEAMS = """
      INSERT INTO FANTA_TOURNAMENTS_TEAMS
      (FANTA_TOURNAMENT_ID, TEAM_ID)
      VALUES(?, ?);
    """.trimIndent()
  }
}
