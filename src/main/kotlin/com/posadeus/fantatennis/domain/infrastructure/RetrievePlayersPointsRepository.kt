package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.PlayerPoints

interface RetrievePlayersPointsRepository {

  fun retrieveBy(tournamentId: Int): List<PlayerPoints>
}
