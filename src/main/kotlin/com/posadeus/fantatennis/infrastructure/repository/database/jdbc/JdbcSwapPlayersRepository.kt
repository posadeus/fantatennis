package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.PlayersToSwapDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.LocalDate

class JdbcSwapPlayersRepository(private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository,
                                private val jdbcTemplate: JdbcTemplate,
                                private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : SwapPlayersRepository {

  override fun swap(teamId: Int, playersToSwap: PlayersToSwapDto): Team =
      when (val fantaTeam = retrieveFantaTeamRepository.retrieve(teamId)) {

        is FoundTeam -> {

          val allPlayers = jdbcTemplate.query(RETRIEVE_ALL_PLAYERS_QUERY, playerRowMapper)

          if (areAllRequestedPlayersPresent(allPlayers.map { it.playerId }, playersToSwap)) {

            val allTournaments = jdbcTemplate.query(RETRIEVE_ALL_TOURNAMENTS_QUERY, tournamentRowMapper)

            if (areAllRequestedTournamentsPresent(allTournaments.map { it.tournamentId }, playersToSwap)) {

              val teamQueryParams = mapOf("teamId" to teamId, "playerIds" to playersToSwap.remove.playerIds)
              val team = namedParameterJdbcTemplate.query(RETRIEVE_TEAM_PK_ID_QUERY, teamQueryParams, teamRowMapper)

              if (team.size != playersToSwap.remove.playerIds.size) ErrorTeam
              else fantaTeam
            }
            else ErrorTeam
          }
          else ErrorTeam
        }

        else -> fantaTeam
      }

  private fun areAllRequestedPlayersPresent(allPlayersIds: List<String>, playersToSwap: PlayersToSwapDto) =
      allPlayersIds.isNotEmpty()
      && allPlayersIds.containsAll(playersToSwap.add.playerIds)
      && allPlayersIds.containsAll(playersToSwap.remove.playerIds)

  private fun areAllRequestedTournamentsPresent(tournamentIds: List<Int>, playersToSwap: PlayersToSwapDto) =
      tournamentIds.isNotEmpty()
      && playersToSwap.add.startingTournamentId in tournamentIds
      && playersToSwap.remove.endingTournamentId in tournamentIds

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
  }
}
