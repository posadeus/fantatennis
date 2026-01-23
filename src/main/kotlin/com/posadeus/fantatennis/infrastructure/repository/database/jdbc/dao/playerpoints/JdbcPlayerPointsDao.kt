package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto.Companion.playersPointsRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcPlayerPointsDao(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : PlayerPointsDao {

  override fun retrieveByTournamentId(tournamentId: Int): List<JdbcPlayerPointsDto> =
      namedParameterJdbcTemplate.query(RETRIEVE_PLAYERS_POINTS_QUERY, mapOf("tournamentId" to tournamentId), playersPointsRowMapper)

  companion object {

    private val RETRIEVE_PLAYERS_POINTS_QUERY = """
      SELECT *
      FROM PLAYERS_POINTS
      WHERE TOURNAMENT_ID = :tournamentId
    """.trimIndent()
  }
}
