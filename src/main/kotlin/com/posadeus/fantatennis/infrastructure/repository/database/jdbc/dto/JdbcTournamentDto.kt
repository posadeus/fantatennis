package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import org.springframework.jdbc.core.RowMapper
import java.time.LocalDate

data class JdbcTournamentDto(val tournamentId: Int,
                             val atpTourId: Int,
                             val tennisTvId: Int,
                             val name: String,
                             val points: Int,
                             val location: String,
                             val surface: String,
                             val year: Int,
                             val startDate: LocalDate,
                             val endDate: LocalDate) {

  companion object {

    val tournamentRowMapper = RowMapper { rs, _ ->
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
  }
}
