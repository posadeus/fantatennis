package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.AtpPlayer

interface PlayerPointsRepository {

  fun save(players: Set<AtpPlayer>)
}
