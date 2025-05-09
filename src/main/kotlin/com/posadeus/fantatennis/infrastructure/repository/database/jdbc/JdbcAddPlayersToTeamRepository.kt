package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.AddPlayersToTeamRepository
import com.posadeus.fantatennis.domain.model.AddPlayers
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.AddPlayersError
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.AddPlayersTeamNotFound
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.FantaTeamDto
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcAddPlayersToTeamRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) : AddPlayersToTeamRepository {

  override fun add(teamId: Int, playerIds: Set<String>, startingTournamentId: Int): AddPlayers {

    val fantaTeamDtos = jdbcTemplate.query(RETRIEVE_FANTA_TEAM_QUERY, mapOf("teamId" to teamId), fantaTeamRowMapper)

    return if (fantaTeamDtos.isEmpty()) AddPlayersTeamNotFound
    else AddPlayersError
  }

  private val fantaTeamRowMapper = RowMapper { rs, _ ->
    FantaTeamDto(teamId = rs.getInt("TEAM_ID"),
                 ownerId = rs.getString("OWNER_ID"))
  }

  companion object {

    private val RETRIEVE_FANTA_TEAM_QUERY = """
      SELECT *
      FROM FANTA_TEAMS
      WHERE TEAM_ID = :teamId;
    """.trimIndent()
  }
}
