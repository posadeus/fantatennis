package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.AtpPlayer
import com.posadeus.fantatennis.domain.model.FantaPointPersistence

interface PersistPlayersPointsRepository {

  fun persistAll(players: Set<AtpPlayer>): FantaPointPersistence
}
