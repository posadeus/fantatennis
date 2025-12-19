package com.posadeus.fantatennis.domain.model

object TestTournament {

  fun aTournament(id: Int = 1,
                  tennisTvId: Int = 300,
                  name: String = "A_NAME",
                  points: Int = 1000,
                  year: Int = 2025): Tournament =
      Tournament(id = id,
                 tennisTvId = tennisTvId,
                 name = name,
                 points = points,
                 year = year)
}