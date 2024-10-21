package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.AddPlayers

interface TeamsRepository {

  fun addPlayers(teamId: Int, playerIds: Set<String>): AddPlayers
}
