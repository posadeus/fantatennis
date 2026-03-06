package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentDto
import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentResultsRepository
import com.posadeus.fantatennis.domain.model.FantaTournamentResults
import com.posadeus.fantatennis.domain.model.FantaTournamentResults.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentResultsDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentResultsDto.Companion.tournamentResultsRowMapper
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcRetrieveFantaTournamentResultsRepository(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate)
  : RetrieveFantaTournamentResultsRepository {

  override fun retrieve(fantaTournamentId: Int): FantaTournamentResults =
      try {

        val params = mapOf("fantaTournamentId" to fantaTournamentId)
        namedParameterJdbcTemplate.query(RETRIEVE_QUERY, params, tournamentResultsRowMapper)
            .takeIf { it.isNotEmpty() }
            ?.let(::toTournamentDto)
            ?.let(::FoundFantaTournamentResults)
        ?: NotFoundFantaTournamentId
      }
      catch (e: RuntimeException) {

        LOGGER.error("Error during retrieve operation for tournament id: $fantaTournamentId", e)
        ErrorFantaTournamentResults
      }

  private fun toTournamentDto(resultsDto: List<JdbcTournamentResultsDto>): FantaTournamentDto =
      resultsDto
          .groupBy(JdbcTournamentResultsDto::teamId)
          .map(::toTeamDto)
          .sortedByDescending(TeamDto::totalScore)
          .let(::FantaTournamentDto)

  private fun toTeamDto(entries: Map.Entry<Int, List<JdbcTournamentResultsDto>>) =
      TeamDto(owner = entries.value.first().ownerId,
              players = entries.value.toSet().map(::toTeamPlayerDto),
              totalScore = calculateTeamTotalScore(entries.value.toSet()))

  private fun calculateTeamTotalScore(tournamentResultsDtoList: Set<JdbcTournamentResultsDto>) =
      tournamentResultsDtoList
          .map(JdbcTournamentResultsDto::playerTotalScore)
          .reduce { teamTotalScore, singlePlayerScore -> teamTotalScore + singlePlayerScore }

  private fun toTeamPlayerDto(dto: JdbcTournamentResultsDto) =
      PlayerPointsDto(fullName = dto.playerFullName,
                      fantaPoints = dto.playerTotalScore)

  companion object {

    private val RETRIEVE_QUERY = """
      SELECT 
        playerStandsForTeam.TEAM_ID, 
        ft2.OWNER_ID,
        playerStandsForTeam.PLAYER_ID, 
        playerPointsByTournament.FULL_NAME, 
        SUM(playerPointsByTournament.FANTA_POINTS) AS TOTAL_SCORE 
      FROM 
      (
        SELECT 
          pp.PLAYER_ID, 
          pl.FULL_NAME, 
          pp.TOURNAMENT_ID, 
          pp.FANTA_POINTS
        FROM PLAYERS_POINTS pp
        LEFT JOIN PLAYERS pl ON pl.PLAYER_ID = pp.PLAYER_ID,
        (
          SELECT 
            ft.STARTING_TOURNAMENT, 
            ft.ENDING_TOURNAMENT, 
            ft.TOURNAMENT_YEAR
          FROM FANTA_TOURNAMENTS ft 
          WHERE ft.FANTA_TOURNAMENT_ID = :fantaTournamentId
        ) AS fttt
        WHERE pp.TOURNAMENT_YEAR = fttt.TOURNAMENT_YEAR 
        AND pp.TOURNAMENT_ID >= fttt.STARTING_TOURNAMENT 
        AND pp.TOURNAMENT_ID <= fttt.ENDING_TOURNAMENT 
        ORDER BY 
          pp.PLAYER_ID ASC, 
          pp.TOURNAMENT_ID ASC
      ) as playerPointsByTournament,
      (
        SELECT 
          tm.TEAM_ID, 
          tm.PLAYER_ID, 
          tm.STARTING_TOURNAMENT, 
          tm.ENDING_TOURNAMENT  
        FROM TEAMS tm 
        WHERE tm.TEAM_ID IN (
          SELECT ftts.TEAM_ID
          FROM FANTA_TOURNAMENTS_TEAMS as ftts
          WHERE ftts.FANTA_TOURNAMENT_ID = :fantaTournamentId
        ) ORDER BY tm.TEAM_ID
      ) as playerStandsForTeam
      LEFT JOIN FANTA_TEAMS ft2 ON playerStandsForTeam.TEAM_ID = ft2.TEAM_ID
      WHERE playerStandsForTeam.PLAYER_ID = playerPointsByTournament.PLAYER_ID 
      AND 
      (
        IF (playerStandsForTeam.ENDING_TOURNAMENT IS NULL,
          playerPointsByTournament.TOURNAMENT_ID >= playerStandsForTeam.STARTING_TOURNAMENT,
          playerPointsByTournament.TOURNAMENT_ID BETWEEN playerStandsForTeam.STARTING_TOURNAMENT and playerStandsForTeam.ENDING_TOURNAMENT)
      )
      GROUP BY 
        playerStandsForTeam.PLAYER_ID, 
        playerStandsForTeam.TEAM_ID
      ORDER BY 
        TEAM_ID ASC, 
        TOTAL_SCORE DESC;
    """.trimIndent()

//    SELECT
//        playerStandsForTeam.TEAM_ID,
//        ft2.OWNER_ID,
//        playerStandsForTeam.PLAYER_ID,
//        playerPointsByTournament.FULL_NAME,
//        SUM(playerPointsByTournament.FANTA_POINTS) AS TOTAL_SCORE
//      FROM
//      (
// PlayerPointsDao.retrieveByTournamentYear(year)
//        SELECT
//          pp.PLAYER_ID,
//          pl.FULL_NAME,
//          pp.TOURNAMENT_ID,
//          pp.FANTA_POINTS
//        FROM PLAYERS_POINTS pp
//        LEFT JOIN PLAYERS pl ON pl.PLAYER_ID = pp.PLAYER_ID,
//        (
// FantaTournamentDao.retrieveBy(fantaTournamentId)
//          SELECT
//            ft.STARTING_TOURNAMENT,
//            ft.ENDING_TOURNAMENT,
//            ft.TOURNAMENT_YEAR
//          FROM FANTA_TOURNAMENTS ft
//          WHERE ft.FANTA_TOURNAMENT_ID = :fantaTournamentId
//--------------- FantaTournamentDao
//        ) AS fttt
//        WHERE pp.TOURNAMENT_YEAR = fttt.TOURNAMENT_YEAR
//        AND pp.TOURNAMENT_ID >= fttt.STARTING_TOURNAMENT
//        AND pp.TOURNAMENT_ID <= fttt.ENDING_TOURNAMENT
//        ORDER BY
//          pp.PLAYER_ID ASC,
//          pp.TOURNAMENT_ID ASC
//      ) as playerPointsByTournament,
//--------------- PlayerPointsDao
//      (
// TeamDao.retrieveBy(teamIds)
//        SELECT
//          tm.TEAM_ID,
//          tm.PLAYER_ID,
//          tm.STARTING_TOURNAMENT,
//          tm.ENDING_TOURNAMENT
//        FROM TEAMS tm
//        WHERE tm.TEAM_ID IN (
// FantaTournamentTeamDao.retrieveBy(fantaTournamentId)
//          SELECT ftts.TEAM_ID
//          FROM FANTA_TOURNAMENTS_TEAMS as ftts
//          WHERE ftts.FANTA_TOURNAMENT_ID = :fantaTournamentId
//-------------- FantaTournamentTeamDao
//        ) ORDER BY tm.TEAM_ID
//      ) as playerStandsForTeam
//-------------- TeamDao
//      LEFT JOIN FANTA_TEAMS ft2 ON playerStandsForTeam.TEAM_ID = ft2.TEAM_ID
//      WHERE playerStandsForTeam.PLAYER_ID = playerPointsByTournament.PLAYER_ID
//      AND
//      (
//        IF (playerStandsForTeam.ENDING_TOURNAMENT IS NULL,
//          playerPointsByTournament.TOURNAMENT_ID >= playerStandsForTeam.STARTING_TOURNAMENT,
//          playerPointsByTournament.TOURNAMENT_ID BETWEEN playerStandsForTeam.STARTING_TOURNAMENT and playerStandsForTeam.ENDING_TOURNAMENT)
//      )
//      GROUP BY
//        playerStandsForTeam.PLAYER_ID,
//        playerStandsForTeam.TEAM_ID
//      ORDER BY
//        TEAM_ID ASC,
//        TOTAL_SCORE DESC;

    private val LOGGER = LoggerFactory.getLogger(JdbcRetrieveFantaTournamentResultsRepository::class.java)
  }
}