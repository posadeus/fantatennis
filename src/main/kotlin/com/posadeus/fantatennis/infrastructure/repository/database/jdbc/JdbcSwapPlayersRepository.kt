package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.app.configuration.infrastructure.OpenForSpring
import com.posadeus.fantatennis.controller.model.team.PlayersToSwapDto
import com.posadeus.fantatennis.domain.exception.InvalidPlayersSwapException
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@OpenForSpring
class JdbcSwapPlayersRepository(private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository,
                                private val jdbcTemplate: JdbcTemplate,
                                private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : SwapPlayersRepository {

  /* TODO add following checks
  * - startingTournamentId must refer to a tournament in the future (right after the end of endingTournament)
  * - endingTournamentId must refer to a tournament in the present (current date should be between start and end dates)
  * - playerToAdd mustn't be already present in the Team
  * - playerToRemove must have endingTournamentId = null in Team table
  */
  // TODO Refactor: move operations inside sub-repositories directly connected to a table
  @Transactional
  override fun swap(teamId: Int, playersToSwap: PlayersToSwapDto): Team =
      when (val fantaTeam = retrieveFantaTeamRepository.retrieve(teamId)) {

        is FoundTeam -> {

          val allPlayers = jdbcTemplate.query(RETRIEVE_ALL_PLAYERS_QUERY, playerRowMapper)

          if (areAllRequestedPlayersPresent(allPlayers.map { it.playerId }, playersToSwap)) {

            val allTournaments = jdbcTemplate.query(RETRIEVE_ALL_TOURNAMENTS_QUERY, tournamentRowMapper)

            if (areAllRequestedTournamentsPresent(allTournaments.map { it.tournamentId }, playersToSwap)) {

              val teamQueryParams = mapOf("teamId" to teamId, "playerIds" to playersToSwap.remove.playerIds)
              val team = namedParameterJdbcTemplate.query(RETRIEVE_TEAM_PK_ID_QUERY, teamQueryParams, teamRowMapper)

              if (team.size != playersToSwap.remove.playerIds.size)
                ErrorTeam
              else
                updateTeam(playersToSwap, teamId)
            }
            else
              ErrorTeam
          }
          else
            ErrorTeam
        }

        else -> fantaTeam
      }

  private fun updateTeam(playersToSwap: PlayersToSwapDto, teamId: Int): Team =
      try {

        val batchInsertQueryParams = playersToSwap.add.playerIds
            .map { mapOf("teamId" to teamId, "playerId" to it, "startingTournamentId" to playersToSwap.add.startingTournamentId) }
        val batchInsertResult = namedParameterJdbcTemplate.batchUpdate(INSERT_TEAM_PLAYERS_QUERY, batchInsertQueryParams.toTypedArray())

        if (batchInsertResult.all { it == 1 }) {

          val batchUpdateQueryParams = playersToSwap.remove.playerIds
              .map { mapOf("teamId" to teamId, "playerId" to it, "endingTournamentId" to playersToSwap.remove.endingTournamentId) }
          val batchUpdateResult = namedParameterJdbcTemplate.batchUpdate(UPDATE_TEAM_PLAYERS_QUERY, batchUpdateQueryParams.toTypedArray())

          if (batchUpdateResult.all { it == 1 })
            retrieveFantaTeamRepository.retrieve(teamId)
          else
            throw InvalidPlayersSwapException(
                error = "Players [${errorPlayers(playersToSwap.remove.playerIds, batchUpdateResult)}] not updated, operation reverted.")
        }
        else
          throw InvalidPlayersSwapException(
              error = "Players [${errorPlayers(playersToSwap.add.playerIds, batchInsertResult)}] not inserted, operation reverted.")
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

  // TODO: can be moved in a support class for repositories
  private fun errorPlayers(team: Set<String>, batchUpdate: IntArray): String {

    val errorIndexes = batchUpdate
        .withIndex()
        .filter { it.value == 0 }
        .map { it.index }

    return team
        .filterIndexed { index, _ -> index in errorIndexes }
        .map { it }
        .reduce { acc, s -> "$acc, $s" }
  }

  private val playerRowMapper = RowMapper { rs, _ ->
    JdbcPlayerDto(playerId = rs.getString("PLAYER_ID"),
                  atpTourId = rs.getString("ATP_TOUR_ID"),
                  fullName = rs.getString("FULL_NAME"))
  }

  private val tournamentRowMapper = RowMapper { rs, _ ->
    JdbcTournamentDto(tournamentId = rs.getInt("TOURNAMENT_ID"),
                      atpTourId = rs.getInt("ATP_TOUR_ID"),
                      tennisTvId = rs.getInt("TENNIS_TV_ID"),
                      name = rs.getString("NAME"),
                      points = rs.getInt("POINTS"),
                      location = rs.getString("LOCATION"),
                      surface = rs.getString("SURFACE"),
                      year = rs.getInt("YEAR"),
                      startDate = LocalDate.parse(rs.getString("START_DATE")),
                      endDate = LocalDate.parse(rs.getString("END_DATE")))
  }

  private val teamRowMapper = RowMapper { rs, _ ->
    JdbcTeamDto(teamId = rs.getInt("TEAM_ID"),
                playerId = rs.getString("PLAYER_ID"),
                startingTournamentId = rs.getInt("STARTING_TOURNAMENT"),
                endingTournamentId = rs.getInt("ENDING_TOURNAMENT"))
  }

  companion object {

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
