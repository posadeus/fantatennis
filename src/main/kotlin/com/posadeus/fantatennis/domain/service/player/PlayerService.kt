package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer

// TODO: create a RetrievePlayerService and a PersistPlayerService to separate responsibilities
class PlayerService(private val retrievePlayersRepository: RetrievePlayersRepository) {

  fun allPlayers(): Set<DomainPlayer> =
      retrievePlayersRepository.retrieve()
}
