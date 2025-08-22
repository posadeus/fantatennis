package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer

// TODO: create a RetrievePlayerService and a PersistPlayerService to separate responsibilities
class PlayerService(private val retrievePlayersRepository: RetrievePlayersRepository,
                    private val persistPlayersRepository: PersistPlayersRepository) {

  fun allPlayers(): Set<DomainPlayer> =
      retrievePlayersRepository.retrieve()

  fun saveAll(players: Set<DomainPlayer>) {

    persistPlayersRepository.persistAll(players)
  }
}
