package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer

class PlayerFantaPointPersistenceService(val repository: PlayerPointsRepository) {

  fun persistScores(players: Set<DomainPlayer>) {

    if (players.isNotEmpty())
      repository.save(players)
  }
}
