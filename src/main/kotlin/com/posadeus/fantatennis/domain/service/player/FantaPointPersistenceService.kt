package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.infrastructure.PlayersRepository
import com.posadeus.fantatennis.domain.model.AtpPlayer
import com.posadeus.fantatennis.domain.model.DomainPlayer
import jakarta.transaction.Transactional

class FantaPointPersistenceService(private val repository: PlayerPointsRepository,
                                   private val playersRepository: PlayersRepository) {

  fun persistScores(players: Set<AtpPlayer>) {

    if (players.isNotEmpty())
      repository.save(players)
  }

  @Transactional
  fun persistPlayersAndScores(domainPlayers: Set<DomainPlayer>,
                              playersScores: Set<AtpPlayer>) {

    playersRepository.saveAll(domainPlayers)
    repository.save(playersScores)
  }
}
