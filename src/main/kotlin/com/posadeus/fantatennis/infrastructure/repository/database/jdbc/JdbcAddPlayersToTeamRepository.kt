package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.app.configuration.infrastructure.OpenForSpring
import com.posadeus.fantatennis.domain.exception.InvalidAddPlayersException
import com.posadeus.fantatennis.domain.infrastructure.AddPlayersToTeamRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.DataBaseErrorManager.manageError
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional

@OpenForSpring
class JdbcAddPlayersToTeamRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) : AddPlayersToTeamRepository {

  @Transactional
  override fun add(teamId: Int, playerIds: Set<String>, startingTournamentId: Int) {

    try {

      val batchQueryParams = playerIds.map { mapOf("teamId" to teamId, "playerId" to it, "startingTournamentId" to startingTournamentId) }
      val batchUpdateResult = jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, batchQueryParams.toTypedArray())

      if (batchUpdateResult.any { it != 1 }) {

          throw InvalidAddPlayersException(error = "Players [${manageError(playerIds, batchUpdateResult) { it }}] not inserted, operation reverted.")
      }
    }
    catch (e: RuntimeException) {

      throw InvalidAddPlayersException(error = "Unexpected error during insert: ${e.message}")
    }
  }

  companion object {

    private val INSERT_PLAYERS_QUERY = """
      INSERT INTO TEAMS
      (TEAM_ID, PLAYER_ID, STARTING_TOURNAMENT)
      VALUES(:teamId, :playerId, :startingTournamentId);
    """.trimIndent()
  }
}
