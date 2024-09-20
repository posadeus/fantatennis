package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.infrastructure.PlayersRepository
import com.posadeus.fantatennis.domain.model.AtpPlayer
import com.posadeus.fantatennis.domain.model.DomainPlayer

class FantaPointPersistenceService(private val repository: PlayerPointsRepository,
                                   private val playersRepository: PlayersRepository) {

  fun persistScores(players: Set<AtpPlayer>) {

    if (players.isNotEmpty())
      repository.save(players)
  }

//  @Transactional
//  TODO(Understand why it is not working in kotlin: Caused by: java.lang.IllegalArgumentException: Cannot subclass final class com.posadeus.fantatennis.domain.service.player.FantaPointPersistenceService)
  fun persistPlayersAndScores(domainPlayers: Set<DomainPlayer>,
                              playersScores: Set<AtpPlayer>) {

    playersRepository.saveAll(domainPlayers)
    repository.save(playersScores)
  }
}
