package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.exception.InvalidPlayerException
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional

class JdbcPersistPlayersRepository(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : PersistPlayersRepository {

  @Transactional
  override fun persistAll(players: Set<DomainPlayer>) {
    try {

      val batchResult = players
          .map(::toEntryParams)
          .let(::persistAll)

      if (batchResult.any { it != 1 })
        throw InvalidPlayerException(error = "Players [${errorPlayers(players, batchResult)}] not inserted, operation reverted.")
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

  // TODO Can be moved outside and generalised
  private fun errorPlayers(players: Set<DomainPlayer>, batchUpdate: IntArray): String {

    val errorIndexes = batchUpdate
        .withIndex()
        .filter { it.value == 0 }
        .map { it.index }

    return players
        .filterIndexed { index, _ -> index in errorIndexes }
        .map { it.id }
        .reduce { acc, s -> "$acc, $s" }
  }

  companion object {

    private val INSERT_PLAYERS_QUERY = """
      INSERT INTO PLAYERS
      (PLAYER_ID, ATP_TOUR_ID, FULL_NAME)
      VALUES(:playerId, :atpTourId, :fullName);
    """.trimIndent()
  }
}
