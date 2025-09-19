package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.app.configuration.infrastructure.OpenForSpring
import com.posadeus.fantatennis.domain.exception.InvalidAddPlayersException
import com.posadeus.fantatennis.domain.infrastructure.AddPlayersToTeamRepository
import com.posadeus.fantatennis.domain.model.AddPlayers
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.*
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto.Companion.fantaTeamRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto.Companion.playerRowMapper
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto.Companion.tournamentRowMapper
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional

@OpenForSpring
class JdbcAddPlayersToTeamRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) : AddPlayersToTeamRepository {

  @Transactional
  override fun add(teamId: Int, playerIds: Set<String>, startingTournamentId: Int): AddPlayers {

    if (!hasResultFor(RETRIEVE_FANTA_TEAM_QUERY, mapOf("teamId" to teamId), fantaTeamRowMapper))
      return AddPlayersTeamNotFound

    if (!hasResultFor(RETRIEVE_TOURNAMENT_QUERY, mapOf("tournamentId" to startingTournamentId), tournamentRowMapper))
      return AddPlayersTournamentNotFound

    val players = jdbcTemplate.query(RETRIEVE_PLAYERS_QUERY, mapOf("playerIds" to playerIds), playerRowMapper)

    if (players.size != playerIds.size)
      return PlayersNotFound(missingPlayerIds = missingPlayerIds(playerIds, players))

    try {

      val batchQueryParams = playerIds.map { mapOf("teamId" to teamId, "playerId" to it, "startingTournamentId" to startingTournamentId) }
      val batchUpdateResult = jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, batchQueryParams.toTypedArray())

      if (batchUpdateResult.all { it == 1 }) {

        return players
            .map(::toDomainPlayer)
            .toSet()
            .let(AddPlayers::ValidAddPlayers)
      }
      else {

        throw InvalidAddPlayersException(error = "Players [${errorPlayers(players, batchUpdateResult)}] not inserted, operation reverted.")
      }
    }
    catch (e: RuntimeException) {

      throw InvalidAddPlayersException(error = "Unexpected error during insert: ${e.message}")
    }
  }

  private fun <T> hasResultFor(query: String, queryParams: Map<String, Int>, rowMapper: RowMapper<T>): Boolean {
    try {

      jdbcTemplate.queryForObject(query, queryParams, rowMapper)
    }
    catch (e: EmptyResultDataAccessException) {

      return false
    }

    return true
  }

  private fun missingPlayerIds(playerIds: Set<String>,
                               players: List<JdbcPlayerDto>) =
      playerIds
          .filter { id -> id !in players.map { it.playerId } }
          .toSet()

  private fun toDomainPlayer(player: JdbcPlayerDto) =
      DomainPlayer(id = player.playerId,
                   atpId = player.atpTourId,
                   fullName = player.fullName)

  private fun errorPlayers(players: List<JdbcPlayerDto>, batchUpdate: IntArray): String {

    val errorIndexes = batchUpdate
        .withIndex()
        .filter { it.value == 0 }
        .map { it.index }

    return players
        .filterIndexed { index, _ -> index in errorIndexes }
        .map { it.playerId }
        .reduce { acc, s -> "$acc, $s" }
  }

  companion object {

    private val RETRIEVE_FANTA_TEAM_QUERY = """
      SELECT *
      FROM FANTA_TEAMS
      WHERE TEAM_ID = :teamId;
    """.trimIndent()

    private val RETRIEVE_TOURNAMENT_QUERY = """
      SELECT *
      FROM TOURNAMENTS 
      WHERE TOURNAMENT_ID = :tournamentId;
    """.trimIndent()

    private val RETRIEVE_PLAYERS_QUERY = """
      SELECT *
      FROM PLAYERS 
      WHERE PLAYER_ID IN (:playerIds);
    """.trimIndent()

    private val INSERT_PLAYERS_QUERY = """
      INSERT INTO TEAMS
      (TEAM_ID, PLAYER_ID, STARTING_TOURNAMENT)
      VALUES(:teamId, :playerId, :startingTournamentId);
    """.trimIndent()
  }
}
