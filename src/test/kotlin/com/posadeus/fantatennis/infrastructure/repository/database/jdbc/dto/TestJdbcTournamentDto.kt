package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto

import java.time.LocalDate

object TestJdbcTournamentDto {

  fun aJdbcTournamentDto(tournamentId: Int = 1,
                         atpTourId: Int = 789,
                         tennisTvId: Int = 789,
                         name: String = "A_NAME",
                         points: Int = 1000,
                         location: String = "A_LOCATION",
                         surface: String = "CLAY",
                         year: Int = 2025,
                         startDate: LocalDate = LocalDate.of(2025, 5, 9),
                         endDate: LocalDate = startDate.plusDays(7)): JdbcTournamentDto =
      JdbcTournamentDto(tournamentId = tournamentId,
                        atpTourId = atpTourId,
                        tennisTvId = tennisTvId,
                        name = name,
                        points = points,
                        location = location,
                        surface = surface,
                        year = year,
                        startDate = startDate,
                        endDate = endDate)
}