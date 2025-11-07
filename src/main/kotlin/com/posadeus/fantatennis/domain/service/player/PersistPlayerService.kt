package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.domain.model.PlayerPersistence

class PersistPlayerService(private val persistPlayersRepository: PersistPlayersRepository) {

  fun persistAll(players: Set<DomainPlayer>): PlayerPersistence =
      persistPlayersRepository.persistAll(players)
}
