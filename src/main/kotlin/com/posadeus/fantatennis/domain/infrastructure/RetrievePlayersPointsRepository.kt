package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.PlayersPoints

interface RetrievePlayersPointsRepository {

  fun retrieveByTournamentId(tournamentId: Int): PlayersPoints
  fun retrieveByYear(year: Int): PlayersPoints
}
