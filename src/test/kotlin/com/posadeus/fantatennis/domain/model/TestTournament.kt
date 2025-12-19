package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.domain.model.Tournament.FoundTournament

object TestTournament {

  fun aTournament(id: Int = 1,
                  tennisTvId: Int = 300,
                  name: String = "A_NAME",
                  points: Int = 1000,
                  year: Int = 2025): FoundTournament =
      FoundTournament(id = id,
                      tennisTvId = tennisTvId,
                      name = name,
                      points = points,
                      year = year)
}