package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.app.configuration.infrastructure.OpenForSpring
import com.posadeus.fantatennis.domain.exception.InvalidPlayerPointsException
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.AtpPlayer
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

      if (batchResult.any { it != 1 })
        throw InvalidPlayerPointsException(error = "PlayersPoints for playerId-tournamentId-year [${errorPlayers(entryParams, batchResult)}] not inserted, operation reverted.")
    }
    catch (e: RuntimeException) {

      throw InvalidPlayerPointsException(error = "Unexpected error during insert: ${e.message}")
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

  // TODO Can be moved outside and generalised
  private fun errorPlayers(players: List<Map<String, Any>>, batchUpdate: IntArray): String {

    val errorIndexes = batchUpdate
        .withIndex()
        .filter { it.value == 0 }
        .map { it.index }

    return players
        .filterIndexed { index, _ -> index in errorIndexes }
        .map { "${it["playerId"]}-${it["tournamentId"]}-${it["tournamentYear"]}" }
        .reduce { acc, s -> "$acc, $s" }
  }

  companion object {

    private val INSERT_PLAYERS_POINTS_QUERY = """
      INSERT INTO PLAYERS_POINTS
      (TOURNAMENT_YEAR, TOURNAMENT_ID, PLAYER_ID, FANTA_POINTS)
      VALUES(:tournamentYear, :tournamentId, :playerId, :fantaPoints);
    """.trimIndent()
  }
}
