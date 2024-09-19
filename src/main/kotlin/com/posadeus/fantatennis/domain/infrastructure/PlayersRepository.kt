package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.DomainPlayer

interface PlayersRepository {

  fun getAllPlayers(): Set<DomainPlayer>
}
