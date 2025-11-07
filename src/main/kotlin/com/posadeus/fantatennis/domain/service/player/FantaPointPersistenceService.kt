package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.domain.model.AtpPlayer
import com.posadeus.fantatennis.domain.model.FantaPointPersistence

class FantaPointPersistenceService(private val persistPlayersPointsRepository: PersistPlayersPointsRepository) {

  fun persistScores(players: Set<AtpPlayer>) {

    if (players.isNotEmpty())
      persistPlayersPointsRepository.persistAll(players)
  }

  fun persist(atpPlayers: Set<AtpPlayer>): FantaPointPersistence {
    TODO("Not yet implemented")
  }
}
