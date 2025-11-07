package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer

class RetrievePlayerService(private val retrievePlayersRepository: RetrievePlayersRepository) {

  fun allPlayers(): Set<DomainPlayer> =
      retrievePlayersRepository.retrieve()
}
