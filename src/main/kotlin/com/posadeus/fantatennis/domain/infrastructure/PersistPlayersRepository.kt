package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.DomainPlayer

interface PersistPlayersRepository {

  fun persistAll(players: Set<DomainPlayer>)
}
