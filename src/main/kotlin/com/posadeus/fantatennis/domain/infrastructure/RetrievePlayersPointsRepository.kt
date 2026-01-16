package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.PlayersPoints

interface RetrievePlayersPointsRepository {

  fun retrieveBy(tournamentId: Int): PlayersPoints
}
