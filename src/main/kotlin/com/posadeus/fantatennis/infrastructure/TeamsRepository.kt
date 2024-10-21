package com.posadeus.fantatennis.infrastructure

import com.posadeus.fantatennis.domain.model.AddPlayers

interface TeamsRepository {

  fun addPlayers(teamId: Int, playerIds: Set<String>): AddPlayers
}
