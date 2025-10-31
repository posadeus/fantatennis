package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.player

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto.Companion.playerRowMapper
import org.springframework.jdbc.core.JdbcTemplate

class JdbcPlayerDao(private val jdbcTemplate: JdbcTemplate) : PlayerDao {

  override fun retrieveAll(): List<JdbcPlayerDto> =
      jdbcTemplate.query(RETRIEVE_PLAYERS_QUERY, playerRowMapper)

  companion object {

    private val RETRIEVE_PLAYERS_QUERY = """
      SELECT *
      FROM PLAYERS;
    """.trimIndent()
  }
}
