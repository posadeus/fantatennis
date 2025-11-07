package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.domain.model.PlayerPersistence

interface PersistPlayersRepository {

  fun persistAll(players: Set<DomainPlayer>): PlayerPersistence
}
