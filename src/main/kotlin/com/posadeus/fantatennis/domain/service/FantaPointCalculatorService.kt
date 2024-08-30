package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.domain.infrastructure.PlayerRepository
import com.posadeus.fantatennis.domain.infrastructure.TournamentRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer

class FantaPointCalculatorService(private val tournamentRepository: TournamentRepository,
                                  private val playerRepository: PlayerRepository) {

  fun calculate(tournamentId: Int, year: Int): Set<DomainPlayer> {
    TODO("Not yet implemented")
  }
}
