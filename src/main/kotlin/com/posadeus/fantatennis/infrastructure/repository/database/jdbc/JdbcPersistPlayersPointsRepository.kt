package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.app.configuration.infrastructure.OpenForSpring
import com.posadeus.fantatennis.domain.exception.InvalidPlayerPointsException
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.AtpPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.DataBaseErrorManager.manageError
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional

@OpenForSpring
class JdbcPersistPlayersPointsRepository(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : PersistPlayersPointsRepository {

  @Transactional
  override fun persistAll(players: Set<AtpPlayer>) {
    try {

      val entryParams = players
          .flatMap(::toEntryParams)

      val batchResult = entryParams
          .let(::persistAll)

      if (batchResult.any { it == 0 })
        throw InvalidPlayerPointsException(message = "PlayersPoints for playerId-tournamentId-year [${manageError(entryParams, batchResult) { "${it["playerId"]}-${it["tournamentId"]}-${it["tournamentYear"]}" }}] not inserted, operation reverted.")
    }
    catch (e: RuntimeException) {

      throw InvalidPlayerPointsException(message = "Unexpected error during insert: ${e.message}")
    }
  }

  private fun persistAll(params: List<Map<String, Any>>): IntArray =
      namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_POINTS_QUERY, params.toTypedArray())

  private fun toEntryParams(player: AtpPlayer): List<Map<String, Any>> =
      player.tournamentPoints
          .entries
          .flatMap { entry ->
            entry.value
                .entries
                .map {
                  mapOf("tournamentYear" to entry.key,
                        "tournamentId" to it.key,
                        "playerId" to player.id,
                        "fantaPoints" to it.value)
                }
          }

  companion object {

    private val INSERT_PLAYERS_POINTS_QUERY = """
      INSERT INTO PLAYERS_POINTS
      (TOURNAMENT_YEAR, TOURNAMENT_ID, PLAYER_ID, FANTA_POINTS)
      VALUES(:tournamentYear, :tournamentId, :playerId, :fantaPoints)
      ON DUPLICATE KEY UPDATE
      FANTA_POINTS = VALUES(FANTA_POINTS);
    """.trimIndent()
  }
}
