package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.app.configuration.infrastructure.OpenForSpring
import com.posadeus.fantatennis.controller.model.team.PlayersToSwapDto
import com.posadeus.fantatennis.domain.exception.InvalidPlayersSwapException
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.Swap.SwapCompleted
import com.posadeus.fantatennis.domain.model.Swap.SwapFailed
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.DataBaseErrorManager.manageError
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.team.CachedTeamDao.Companion.invalidateKeysContaining
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto.Companion.playerRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto.Companion.teamRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto.Companion.tournamentRowMapper
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional

@OpenForSpring
class JdbcSwapPlayersRepository(private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository,
                                private val jdbcTemplate: JdbcTemplate,
                                private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
                                private val teamCache: Cache<Set<TeamId>, List<JdbcTeamDto>>) : SwapPlayersRepository {

  /* TODO add following checks
  * - startingTournamentId must refer to a tournament in the future (right after the end of endingTournament)
  * - endingTournamentId must refer to a tournament in the present (current date should be between start and end dates)
  * - playerToAdd mustn't be already present in the Team without an endingTournamentId
  * - playerToRemove must have endingTournamentId = null in Team table
  */
  // TODO Refactor: move operations inside sub-repositories directly connected to a table
  @Transactional
  override fun swap(teamId: Int, playersToSwap: PlayersToSwapDto): Swap =
      when (retrieveFantaTeamRepository.retrieve(teamId)) {

        is FoundTeam -> {

          val allPlayers = jdbcTemplate.query(RETRIEVE_ALL_PLAYERS_QUERY, playerRowMapper)

          if (areAllRequestedPlayersPresent(allPlayers.map { it.playerId }, playersToSwap)) {

            val allTournaments = jdbcTemplate.query(RETRIEVE_ALL_TOURNAMENTS_QUERY, tournamentRowMapper)

            if (areAllRequestedTournamentsPresent(allTournaments.map { it.tournamentId }, playersToSwap)) {

              val teamQueryParams = mapOf("teamId" to teamId, "playerIds" to playersToSwap.remove.playerIds)
              val team = namedParameterJdbcTemplate.query(RETRIEVE_TEAM_PK_ID_QUERY, teamQueryParams, teamRowMapper)

              if (team.size != playersToSwap.remove.playerIds.size)
                SwapFailed
                    .also { LOGGER.error("One or more team players not found") } // TODO add players
              else
                updateTeam(playersToSwap, teamId)
            }
            else
              SwapFailed
                .also { LOGGER.error("One or more requested tournaments not found.") } // TODO add tournaments
          }
          else
            SwapFailed
                .also { LOGGER.error("One or more requested players not found.") } // TODO add players
        }

        else -> SwapFailed
            .also { LOGGER.error("Team not found: $teamId.") }
      }

  private fun updateTeam(playersToSwap: PlayersToSwapDto, teamId: Int): Swap =
      try {

        val batchInsertQueryParams = playersToSwap.add.playerIds
            .map { mapOf("teamId" to teamId, "playerId" to it, "startingTournamentId" to playersToSwap.add.startingTournamentId) }
        val batchInsertResult = namedParameterJdbcTemplate.batchUpdate(INSERT_TEAM_PLAYERS_QUERY, batchInsertQueryParams.toTypedArray())

        if (batchInsertResult.all { it == 1 }) {

          val batchUpdateQueryParams = playersToSwap.remove.playerIds
              .map { mapOf("teamId" to teamId, "playerId" to it, "endingTournamentId" to playersToSwap.remove.endingTournamentId) }
          val batchUpdateResult = namedParameterJdbcTemplate.batchUpdate(UPDATE_TEAM_PLAYERS_QUERY, batchUpdateQueryParams.toTypedArray())

          if (batchUpdateResult.all { it == 1 })
            SwapCompleted
                .also { invalidateKeysContaining(teamCache, setOf(teamId)) }
          else
            throw InvalidPlayersSwapException(
                error = "Players [${manageError(playersToSwap.remove.playerIds, batchUpdateResult) { it }}] not updated, operation reverted.")
        }
        else
          throw InvalidPlayersSwapException(
              error = "Players [${manageError(playersToSwap.add.playerIds, batchInsertResult) { it }}] not inserted, operation reverted.")
      }
      catch (e: RuntimeException) {

        throw InvalidPlayersSwapException(error = "Unexpected error during insert/update: ${e.message}")
      }

  private fun areAllRequestedPlayersPresent(allPlayersIds: List<String>, playersToSwap: PlayersToSwapDto) =
      allPlayersIds.isNotEmpty()
      && allPlayersIds.containsAll(playersToSwap.add.playerIds)
      && allPlayersIds.containsAll(playersToSwap.remove.playerIds)

  private fun areAllRequestedTournamentsPresent(tournamentIds: List<Int>, playersToSwap: PlayersToSwapDto) =
      tournamentIds.isNotEmpty()
      && playersToSwap.add.startingTournamentId in tournamentIds
      && playersToSwap.remove.endingTournamentId in tournamentIds

  companion object {

    private val LOGGER = LoggerFactory.getLogger(JdbcSwapPlayersRepository::class.java)

    private val RETRIEVE_ALL_PLAYERS_QUERY = """
      SELECT *
      FROM PLAYERS p;
    """.trimIndent()

    private val RETRIEVE_ALL_TOURNAMENTS_QUERY = """
      SELECT *
      FROM TOURNAMENTS t;
    """.trimIndent()

    private val RETRIEVE_TEAM_PK_ID_QUERY = """
      SELECT *
      FROM TEAMS t
      WHERE t.TEAM_ID = :teamId
      AND t.PLAYER_ID IN (:playerIds);
    """.trimIndent()

    private val INSERT_TEAM_PLAYERS_QUERY = """
      INSERT INTO TEAMS
      (TEAM_ID, PLAYER_ID, STARTING_TOURNAMENT)
      VALUES(:teamId, :playerId, :startingTournamentId);
    """.trimIndent()

    private val UPDATE_TEAM_PLAYERS_QUERY = """
      UPDATE TEAMS
      SET ENDING_TOURNAMENT = :endingTournamentId
      WHERE TEAM_ID = :teamId AND PLAYER_ID = :playerId;
    """.trimIndent()
  }
}
