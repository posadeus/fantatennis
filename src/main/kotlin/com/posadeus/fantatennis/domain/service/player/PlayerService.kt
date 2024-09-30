package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer

class PlayerService(private val playersRepository: PlayersRepository) {

  fun allPlayers(): Set<DomainPlayer> =
      playersRepository.getAllPlayers()

  fun saveAll(players: Set<DomainPlayer>) {
    TODO("Not yet implemented")
  }
}
