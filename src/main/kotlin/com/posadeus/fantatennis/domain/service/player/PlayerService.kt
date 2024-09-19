package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PlayerRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer

class PlayerService(private val playerRepository: PlayerRepository) {

  fun allPlayers(): Set<DomainPlayer> =
      playerRepository.getAllPlayers()
}
