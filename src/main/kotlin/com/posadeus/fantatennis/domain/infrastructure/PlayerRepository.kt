package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.DomainPlayer

interface PlayerRepository {

  fun getAllPlayers(): Set<DomainPlayer>
}
