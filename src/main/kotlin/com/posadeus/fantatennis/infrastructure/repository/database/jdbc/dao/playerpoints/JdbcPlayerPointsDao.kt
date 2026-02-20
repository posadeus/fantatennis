package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints

import com.posadeus.fantatennis.app.configuration.infrastructure.OpenForSpring
import com.posadeus.fantatennis.domain.exception.InvalidPlayerPointsException
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.DataBaseErrorManager.manageError
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto.Companion.playersPointsRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional

@OpenForSpring
class JdbcPlayerPointsDao(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : PlayerPointsDao {

  override fun retrieveByTournamentId(tournamentId: Int): List<JdbcPlayerPointsDto> =
      mapOf("tournamentId" to tournamentId)
          .let {
            namedParameterJdbcTemplate.query(RETRIEVE_PLAYERS_POINTS_BY_TOURNAMENT_ID_QUERY, it, playersPointsRowMapper)
          }

  override fun retrieveByTournamentYear(year: Int): List<JdbcPlayerPointsDto> =
      mapOf("tournamentYear" to year)
          .let {
            namedParameterJdbcTemplate.query(RETRIEVE_PLAYERS_POINTS_BY_TOURNAMENT_YEAR_QUERY, it, playersPointsRowMapper)
          }


  @Transactional
  override fun persistAll(players: List<JdbcPlayerPointsDto>) {

    val entryParams = players.map(::toEntryParams)

    try {

      entryParams
          .let { namedParameterJdbcTemplate.batchUpdate(INSERT_PLAYERS_POINTS_QUERY, it.toTypedArray()) }
          .takeIf { batchResult -> batchResult.any { it == 0 } }
          ?.let { throw exceptionManagement(entryParams, it) }
    }
    catch (e: RuntimeException) {

      throw InvalidPlayerPointsException(message = e.message ?: "Unexpected Error")
    }
  }

  private fun exceptionManagement(entryParams: List<Map<String, Any>>, batchResult: IntArray) =
      InvalidPlayerPointsException(message =
         "PlayersPoints for playerId-tournamentId-year " +
         "[${manageError(entryParams, batchResult) { "${it["playerId"]}-${it["tournamentId"]}-${it["tournamentYear"]}" }}] " +
         "not inserted, operation reverted.")

  private fun toEntryParams(playerPoints: JdbcPlayerPointsDto): Map<String, Any> =
      mapOf("tournamentYear" to playerPoints.tournamentYear,
            "tournamentId" to playerPoints.tournamentId,
            "playerId" to playerPoints.playerId,
            "fantaPoints" to playerPoints.fantaPoints)

  companion object {

    private val RETRIEVE_PLAYERS_POINTS_BY_TOURNAMENT_ID_QUERY = """
      SELECT *
      FROM PLAYERS_POINTS
      WHERE TOURNAMENT_ID = :tournamentId
    """.trimIndent()

    private val RETRIEVE_PLAYERS_POINTS_BY_TOURNAMENT_YEAR_QUERY = """
      SELECT *
      FROM PLAYERS_POINTS
      WHERE 'YEAR' = :tournamentYear
    """.trimIndent()

    private val INSERT_PLAYERS_POINTS_QUERY = """
      INSERT INTO PLAYERS_POINTS
      (TOURNAMENT_YEAR, TOURNAMENT_ID, PLAYER_ID, FANTA_POINTS)
      VALUES(:tournamentYear, :tournamentId, :playerId, :fantaPoints)
      ON DUPLICATE KEY UPDATE
      FANTA_POINTS = VALUES(FANTA_POINTS);
    """.trimIndent()
  }
}
