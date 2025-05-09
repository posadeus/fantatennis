package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.AddPlayersToTeamRepository
import com.posadeus.fantatennis.domain.model.AddPlayers
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.*
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.LocalDate

class JdbcAddPlayersToTeamRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) : AddPlayersToTeamRepository {

  override fun add(teamId: Int, playerIds: Set<String>, startingTournamentId: Int): AddPlayers {

    try {

      val fantaTeamDto = jdbcTemplate.queryForObject(RETRIEVE_FANTA_TEAM_QUERY, mapOf("teamId" to teamId), fantaTeamRowMapper)
    }
    catch (e: EmptyResultDataAccessException) {

      return AddPlayersTeamNotFound
    }

    try {

      val tournamentDto = jdbcTemplate.queryForObject(RETRIEVE_TOURNAMENT_QUERY,
                                                      mapOf("tournamentId" to startingTournamentId),
                                                      tournamentRowMapper)
    }
    catch (e: EmptyResultDataAccessException) {

      return AddPlayersTournamentNotFound
    }

    val players = jdbcTemplate.query(RETRIEVE_PLAYERS_QUERY, mapOf("playerIds" to playerIds), playerRowMapper)

    if (players.size != playerIds.size) return PlayersNotFound(missingPlayerIds = missingPlayerIds(playerIds, players))

    return AddPlayersError
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
  }
}
