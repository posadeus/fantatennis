package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.PlayersToSwapDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.time.LocalDate

class JdbcSwapPlayersRepository(private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository,
                                private val jdbcTemplate: JdbcTemplate) : SwapPlayersRepository {

  override fun swap(teamId: Int, playersToSwap: PlayersToSwapDto): Team =
      when (val team = retrieveFantaTeamRepository.retrieve(teamId)) {

        is FoundTeam -> {

          val allPlayers = jdbcTemplate.query(RETRIEVE_ALL_PLAYERS_QUERY, playerRowMapper)

          if (allPlayers.isEmpty()) ErrorTeam
          else if (areAllRequestedPlayersPresent(allPlayers.map { it.playerId }, playersToSwap)) {

            val allTournaments = jdbcTemplate.query(RETRIEVE_ALL_TOURNAMENTS_QUERY, tournamentRowMapper)

            if (areAllRequestedTournamentsPresent(allTournaments.map { it.tournamentId }, playersToSwap)) team
            else ErrorTeam
          }
          else ErrorTeam
        }

        else -> team
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

  companion object {

    private val RETRIEVE_ALL_PLAYERS_QUERY = """
      SELECT *
      FROM PLAYERS p;
    """.trimIndent()

    private val RETRIEVE_ALL_TOURNAMENTS_QUERY = """
      SELECT *
      FROM TOURNAMENTS t;
    """.trimIndent()
  }
}
