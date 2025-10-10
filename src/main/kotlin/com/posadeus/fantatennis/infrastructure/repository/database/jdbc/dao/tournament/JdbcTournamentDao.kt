package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto.Companion.tournamentRowMapper
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.Types

class JdbcTournamentDao(private val jdbcTemplate: JdbcTemplate) : TournamentDao {

  override fun retrieveAllBy(year: Int): List<JdbcTournamentDto> =
      try {

        jdbcTemplate.query(RETRIEVE_TOURNAMENTS_QUERY, arrayOf(year), intArrayOf(Types.INTEGER), tournamentRowMapper)
      }
      catch (e: RuntimeException) {

        LOGGER.error("Error during retrieve operation")
        emptyList()
      }

  companion object {

    private val LOGGER = LoggerFactory.getLogger(JdbcTournamentDao::class.java)

    private val RETRIEVE_TOURNAMENTS_QUERY = """
      SELECT *
      FROM TOURNAMENTS
      WHERE `YEAR` = ?;
    """.trimIndent()
  }
}
