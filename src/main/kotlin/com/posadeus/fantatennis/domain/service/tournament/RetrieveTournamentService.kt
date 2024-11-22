package com.posadeus.fantatennis.domain.service.tournament

import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournamentResults

class RetrieveTournamentService(private val fantaTournamentsRepository: FantaTournamentsRepository) {

  fun retrieve(tournamentId: Int): FantaTournamentResults =
      fantaTournamentsRepository.retrieveTournamentResults(tournamentId)
}
