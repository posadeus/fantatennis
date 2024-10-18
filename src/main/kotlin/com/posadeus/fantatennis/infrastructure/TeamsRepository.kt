package com.posadeus.fantatennis.infrastructure

import com.posadeus.fantatennis.domain.model.DomainPlayer

interface TeamsRepository {

  fun addPlayers(teamId: Int, playerIds: Set<String>): Set<DomainPlayer>
}
