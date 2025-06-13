package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.PlayersToSwapDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

class JdbcSwapPlayersRepository(private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository,
                                private val jdbcTemplate: JdbcTemplate) : SwapPlayersRepository {

  override fun swap(teamId: Int, playersToSwap: PlayersToSwapDto): Team =
      when (val team = retrieveFantaTeamRepository.retrieve(teamId)) {

        is FoundTeam -> {

          val allPlayers = jdbcTemplate.query(RETRIEVE_ALL_PLAYERS_QUERY, playerRowMapper)

          if (allPlayers.isEmpty()) ErrorTeam
          else team
        }

        else -> team
      }

  private val playerRowMapper = RowMapper { rs, _ ->
    JdbcPlayerDto(playerId = rs.getString("PLAYER_ID"),
                  atpTourId = rs.getString("ATP_TOUR_ID"),
                  fullName = rs.getString("FULL_NAME"))
  }

  companion object {

    private val RETRIEVE_ALL_PLAYERS_QUERY = """
      SELECT *
      FROM PLAYERS p;
    """.trimIndent()
  }
}
