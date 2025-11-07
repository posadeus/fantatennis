package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.player

import com.posadeus.fantatennis.app.configuration.infrastructure.OpenForSpring
import com.posadeus.fantatennis.domain.exception.InvalidPlayerException
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.DataBaseErrorManager.manageError
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto.Companion.playerRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional

@OpenForSpring
class JdbcPlayerDao(private val jdbcTemplate: NamedParameterJdbcTemplate) : PlayerDao {

  override fun retrieveAll(): List<JdbcPlayerDto> =
      jdbcTemplate.query(RETRIEVE_PLAYERS_QUERY, playerRowMapper)

  @Transactional
  override fun persistAll(players: Set<JdbcPlayerDto>) {

    players
        .map(::toEntryParams)
        .let(::persistAll)
        .let { batchResult ->
          batchResult
              .any(::hasFailed)
              .takeIf { it }
              ?.let {
                throw InvalidPlayerException(error = "Players [${manageError(players, batchResult) { it.playerId }}] not inserted, operation reverted.")
              }
        }
  }

  private fun toEntryParams(player: JdbcPlayerDto): Map<String, Any> =
      mapOf("playerId" to player.playerId,
            "atpTourId" to player.atpTourId,
            "fullName" to player.fullName)

  private fun persistAll(params: List<Map<String, Any>>): IntArray =
      jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, params.toTypedArray())

  private fun hasFailed(batchResult: Int) =
      batchResult != 1

  companion object {

    private val RETRIEVE_PLAYERS_QUERY = """
      SELECT *
      FROM PLAYERS;
    """.trimIndent()

    private val INSERT_PLAYERS_QUERY = """
      INSERT INTO PLAYERS
      (PLAYER_ID, ATP_TOUR_ID, FULL_NAME)
      VALUES(:playerId, :atpTourId, :fullName);
    """.trimIndent()
  }
}
