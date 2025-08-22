package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.AtpPlayer

interface PersistPlayersPointsRepository {

  fun persistAll(players: Set<AtpPlayer>)
}
