package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentResultsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TournamentResultsDto
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class JdbcRetrieveFantaTournamentResultsRepository(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate)
  : RetrieveFantaTournamentResultsRepository {

  override fun retrieve(tournamentId: Int): FantaTournamentResults =
      try {

        val params = mapOf("tournamentId" to tournamentId)
        namedParameterJdbcTemplate.query(RETRIEVE_QUERY, params, rowMapper)
            .takeIf { it.isNotEmpty() }
            ?.let(::toTournamentDto)
            ?.let(::FoundFantaTournamentResults)
        ?: NotFoundFantaTournamentId
      }
      catch (e: RuntimeException) {

        LOGGER.error("Error during retrieve operation for tournament id: $tournamentId", e)
        ErrorFantaTournamentResults
      }

  private val rowMapper = RowMapper { rs, _ ->
    TournamentResultsDtoImpl(tournamentId = rs.getInt("FANTA_TOURNAMENT_ID"),
                             teamId = rs.getInt("TEAM_ID"),
                             ownerId = rs.getString("OWNER_ID"),
                             playerId = rs.getString("PLAYER_ID"),
                             playerFullName = rs.getString("FULL_NAME"),
                             playerTotalScore = rs.getDouble("TOTAL_SCORE"))
  }

  private fun toTournamentDto(resultsDto: List<TournamentResultsDto>): TournamentDto =
      resultsDto
          .groupBy(TournamentResultsDto::getTeamId)
          .map(::toTeamDto)
          .sortedByDescending(TeamDto::totalScore)
          .let(::TournamentDto)

  private fun toTeamDto(entries: Map.Entry<Int, List<TournamentResultsDto>>) =
      TeamDto(owner = entries.value.first().getOwnerId(),
              players = entries.value.map(::toTeamPlayerDto),
              totalScore = calculateTeamTotalScore(entries.value))

  private fun calculateTeamTotalScore(tournamentResultsDtoList: List<TournamentResultsDto>) =
      tournamentResultsDtoList
          .map(TournamentResultsDto::getPlayerTotalScore)
          .reduce { teamTotalScore, singlePlayerScore -> teamTotalScore + singlePlayerScore }

  private fun toTeamPlayerDto(dto: TournamentResultsDto) =
      TeamPlayerDto(fullName = dto.getPlayerFullName(),
                    fantaPoints = dto.getPlayerTotalScore())

  // FIXME: remove interface and all the methods and change it to data class
  // FIXME: move it outside
  class TournamentResultsDtoImpl(private val tournamentId: Int,
                                 private val teamId: Int,
                                 private val ownerId: String,
                                 private val playerId: String,
                                 private val playerFullName: String,
                                 private val playerTotalScore: Double) : TournamentResultsDto {

    override fun getTournamentId(): Int = tournamentId

    override fun getTeamId(): Int = teamId

    override fun getOwnerId(): String = ownerId

    override fun getPlayerId(): String = playerId

    override fun getPlayerFullName(): String = playerFullName

    override fun getPlayerTotalScore(): Double = playerTotalScore
  }

  companion object {

    private val RETRIEVE_QUERY = """
      SELECT ft.FANTA_TOURNAMENT_ID, ft2.TEAM_ID, p.FULL_NAME, p.PLAYER_ID, pps.TOTAL_SCORE, ft2.OWNER_ID 
      FROM fanta_tennis.FANTA_TOURNAMENTS ft 
        LEFT JOIN fanta_tennis.FANTA_TOURNAMENTS_TEAMS ftt ON ft.FANTA_TOURNAMENT_ID = ftt.FANTA_TOURNAMENT_ID 
        LEFT JOIN fanta_tennis.FANTA_TEAMS ft2 ON ftt.TEAM_ID = ft2.TEAM_ID
        LEFT JOIN fanta_tennis.TEAMS t ON ft2.TEAM_ID = t.TEAM_ID
        LEFT JOIN fanta_tennis.PLAYERS p ON t.PLAYER_ID = p.PLAYER_ID
        LEFT JOIN (	
          SELECT 
            pp.PLAYER_ID AS ID,
            SUM(pp.FANTA_POINTS) AS TOTAL_SCORE
          FROM 
            fanta_tennis.PLAYERS_POINTS pp
            LEFT JOIN fanta_tennis.TEAMS t2 ON pp.PLAYER_ID = t2.PLAYER_ID
            LEFT JOIN fanta_tennis.FANTA_TOURNAMENTS_TEAMS ftt2 ON ftt2.TEAM_ID = t2.TEAM_ID
            LEFT JOIN fanta_tennis.FANTA_TOURNAMENTS fto ON ftt2.FANTA_TOURNAMENT_ID = fto.FANTA_TOURNAMENT_ID 
          WHERE 
            fto.FANTA_TOURNAMENT_ID = :tournamentId
            AND pp.TOURNAMENT_YEAR = fto.TOURNAMENT_YEAR
            AND t2.STARTING_TOURNAMENT BETWEEN fto.STARTING_TOURNAMENT AND fto.ENDING_TOURNAMENT
            AND pp.TOURNAMENT_ID BETWEEN t2.STARTING_TOURNAMENT AND fto.ENDING_TOURNAMENT
            AND (t2.ENDING_TOURNAMENT IS NULL OR pp.TOURNAMENT_ID <= t2.ENDING_TOURNAMENT) 
          GROUP BY pp.PLAYER_ID
        ) AS pps ON pps.ID = p.PLAYER_ID 
      WHERE ft.FANTA_TOURNAMENT_ID = :tournamentId
      ORDER BY t.TEAM_ID, pps.TOTAL_SCORE DESC;
    """.trimIndent()

    private val LOGGER = LoggerFactory.getLogger(JdbcRetrieveFantaTournamentResultsRepository::class.java)
  }
}