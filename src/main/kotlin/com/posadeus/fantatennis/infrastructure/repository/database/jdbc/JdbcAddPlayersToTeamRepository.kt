package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.AddPlayersToTeamRepository
import com.posadeus.fantatennis.domain.model.AddPlayers
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.*
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.*
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

class JdbcAddPlayersToTeamRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) : AddPlayersToTeamRepository {

  @Transactional
  override fun add(teamId: Int, playerIds: Set<String>, startingTournamentId: Int): AddPlayers {

    try {

      jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, mapOf("teamId" to teamId), fantaTeamRowMapper)
    }
    catch (e: EmptyResultDataAccessException) {

      return AddPlayersTeamNotFound
    }

    try {

      jdbcTemplate.queryForObject(RETRIEVE_TOURNAMENT_QUERY, mapOf("tournamentId" to startingTournamentId), tournamentRowMapper)
    }
    catch (e: EmptyResultDataAccessException) {

      return AddPlayersTournamentNotFound
    }

    val players = jdbcTemplate.query(RETRIEVE_PLAYERS_QUERY, mapOf("playerIds" to playerIds), playerRowMapper)

    if (players.size != playerIds.size)
      return PlayersNotFound(missingPlayerIds = missingPlayerIds(playerIds, players))

    try {

      val batchValues = playerIds.map { mapOf("teamId" to teamId, "playerId" to it, "startingTournamentId" to startingTournamentId) }
      val batchUpdate = jdbcTemplate.batchUpdate(INSERT_PLAYERS_QUERY, batchValues.toTypedArray())

      if (batchUpdate.all { it == 1 })
        return players
            .map(::toDomainPlayer)
            .toSet()
            .let(AddPlayers::ValidAddPlayers)
      else
        throw AddPlayersException(error = "Players [${errorPlayers(players, batchUpdate)}] not inserted, operation reverted.")
    }
    catch (e: RuntimeException) {

      throw AddPlayersException(error = "Unexpected error during insert: ${e.message}")
    }
  }

  private val fantaTeamRowMapper = RowMapper { rs, _ ->
    FantaTeamDto(teamId = rs.getInt("TEAM_ID"),
                 ownerId = rs.getString("OWNER_ID"))
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

  private val playerRowMapper = RowMapper { rs, _ ->
    JdbcPlayerDto(playerId = rs.getString("PLAYER_ID"),
                  atpTourId = rs.getString("ATP_TOUR_ID"),
                  fullName = rs.getString("FULL_NAME"))
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
      WHERE PLAYER_ID IN :playerIds;
    """.trimIndent()

    private val INSERT_PLAYERS_QUERY = """
      INSERT INTO TEAMS
      (TEAM_ID, PLAYER_ID, STARTING_TOURNAMENT)
      VALUES(:teamId, :playerId, :startingTournamentId);
    """.trimIndent()
  }
}
