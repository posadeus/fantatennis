package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.model.AtpPlayer

class FantaPointPersistenceService(private val repository: PlayerPointsRepository) {

  fun persistScores(players: Set<AtpPlayer>) {

    if (players.isNotEmpty())
      repository.save(players)
  }
}
