package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.DomainPlayer

interface RetrievePlayersRepository {

  fun retrieve(): Set<DomainPlayer>
}
