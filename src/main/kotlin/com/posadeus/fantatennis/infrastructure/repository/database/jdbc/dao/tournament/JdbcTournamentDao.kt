package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto.Companion.tournamentRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.Types

class JdbcTournamentDao(private val jdbcTemplate: JdbcTemplate) : TournamentDao {

  override fun retrieveAllBy(year: Int): List<JdbcTournamentDto> =
      jdbcTemplate.query(RETRIEVE_TOURNAMENTS_QUERY, arrayOf(year), intArrayOf(Types.INTEGER), tournamentRowMapper)

  companion object {

    private val RETRIEVE_TOURNAMENTS_QUERY = """
      SELECT *
      FROM TOURNAMENTS
      WHERE `YEAR` = ?;
    """.trimIndent()
  }
}
