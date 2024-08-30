package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.DomainPlayer

interface PlayerPointsRepository {

  fun save(players: Set<DomainPlayer>)
}
