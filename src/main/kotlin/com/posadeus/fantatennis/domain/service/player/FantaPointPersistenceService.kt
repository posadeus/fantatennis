package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.AtpPlayer

class FantaPointPersistenceService(private val persistPlayersPointsRepository: PersistPlayersPointsRepository) {

  fun persistScores(players: Set<AtpPlayer>) {

    if (players.isNotEmpty())
      persistPlayersPointsRepository.persistAll(players)
  }
}
