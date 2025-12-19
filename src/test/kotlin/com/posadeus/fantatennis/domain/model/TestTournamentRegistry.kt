package com.posadeus.fantatennis.domain.model

object TestTournamentRegistry {

  fun aTournamentRegistry(atpTourId: Int = 1,
                          tennisTvId: Int = 1,
                          name: String = "A_NAME",
                          startDate: String = "2025-12-01",
                          endDate: String = "2025-12-07",
                          year: Int = 2025,
                          points: Int = 250,
                          surface: Surface = Surface.HARD,
                          location: String = "A_LOCATION") =
      TournamentRegistry(atpTourId = atpTourId,
                         tennisTvId = tennisTvId,
                         name = name,
                         startDate = startDate,
                         endDate = endDate,
                         year = year,
                         points = points,
                         surface = surface,
                         location = location)
}