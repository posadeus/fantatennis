package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveTournamentsRepository
import com.posadeus.fantatennis.domain.model.Tournament
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.Types
import java.time.LocalDate

class JdbcRetrieveTournamentsRepository(private val jdbcTemplate: JdbcTemplate) : RetrieveTournamentsRepository {

  override fun retrieveAllBy(year: Int): List<Tournament> =
      try {

        year
            .let(::retrieveAllByYear)
            .map(::toTournament)
      }
      catch (e: RuntimeException) {

        LOGGER.error("Error during retrieve operation")
        emptyList()
      }

  private fun retrieveAllByYear(year: Int): MutableList<JdbcTournamentDto> =
      jdbcTemplate.query(RETRIEVE_TOURNAMENTS_QUERY, arrayOf(year), intArrayOf(Types.INTEGER), tournamentRowMapper)

  private fun toTournament(dto: JdbcTournamentDto): Tournament =
      Tournament(id = dto.tournamentId,
                 tennisTvId = dto.tennisTvId,
                 points = dto.points,
                 year = dto.year)

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

    private val LOGGER = LoggerFactory.getLogger(JdbcRetrieveTournamentsRepository::class.java)

    private val RETRIEVE_TOURNAMENTS_QUERY = """
      SELECT TOURNAMENT_ID, ATP_TOUR_ID, TENNIS_TV_ID, NAME, POINTS, LOCATION, SURFACE, `YEAR`, START_DATE, END_DATE
      FROM TOURNAMENTS
      WHERE `YEAR` = ?;
    """.trimIndent()
  }
}
