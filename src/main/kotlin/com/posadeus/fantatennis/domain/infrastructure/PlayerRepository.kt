package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.DomainPlayer

interface PlayerRepository {

  fun getPlayers(): Set<DomainPlayer>
}
