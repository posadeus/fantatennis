package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.app.configuration.infrastructure.OpenForSpring
import com.posadeus.fantatennis.domain.exception.InvalidPlayerException
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.DataBaseErrorManager.manageError
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional

@OpenForSpring
class JdbcPersistPlayersRepository(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : PersistPlayersRepository {

  @Transactional
  override fun persistAll(players: Set<DomainPlayer>) {
    try {

      val batchResult = players
          .map(::toEntryParams)
          .let(::persistAll)

      if (batchResult.any { it != 1 })
        throw InvalidPlayerException(error = "Players [${manageError(players, batchResult) { it.id }}] not inserted, operation reverted.")
    }
    catch (e: RuntimeException) {

      throw InvalidPlayerException(error = "Unexpected error during insert: ${e.message}")
    }
  }

  private fun persistAll(params: List<Map<String, Any>>): IntArray =
      namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, params.toTypedArray())

  private fun toEntryParams(player: DomainPlayer): Map<String, Any> =
      mapOf("playerId" to player.id,
            "atpTourId" to player.atpId,
            "fullName" to player.fullName)

  companion object {

    private val INSERT_PLAYERS_QUERY = """
      INSERT INTO PLAYERS
      (PLAYER_ID, ATP_TOUR_ID, FULL_NAME)
      VALUES(:playerId, :atpTourId, :fullName);
    """.trimIndent()
  }
}
