package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

class JdbcRetrievePlayersRepository(private val jdbcTemplate: JdbcTemplate) : RetrievePlayersRepository {

  override fun retrieve(): Set<DomainPlayer> =
      try {

        jdbcTemplate.query(RETRIEVE_PLAYERS_QUERY, playerRowMapper)
            .map(::toDomainPlayer)
            .toSet()
      }
      catch (e: RuntimeException) {

        LOGGER.error("Error retrieving players", e)
        emptySet()
      }

  private val playerRowMapper = RowMapper { rs, _ ->
    JdbcPlayerDto(playerId = rs.getString("PLAYER_ID"),
                  atpTourId = rs.getString("ATP_TOUR_ID"),
                  fullName = rs.getString("FULL_NAME"),
                  rolandGarrosId = rs.getBigDecimal("RG_ID"))
  }

  private fun toDomainPlayer(player: JdbcPlayerDto) =
      DomainPlayer(id = player.playerId,
                   atpId = player.atpTourId,
                   fullName = player.fullName,
                   rolandGarrosId = player.rolandGarrosId)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(JdbcRetrievePlayersRepository::class.java)

    private val RETRIEVE_PLAYERS_QUERY = """
      SELECT *
      FROM PLAYERS;
    """.trimIndent()
  }
}
