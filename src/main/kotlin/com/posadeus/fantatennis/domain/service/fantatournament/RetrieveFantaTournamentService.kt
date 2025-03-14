package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournamentResults

class RetrieveFantaTournamentService(private val fantaTournamentsRepository: FantaTournamentsRepository) {

  fun retrieve(tournamentId: Int): FantaTournamentResults =
      fantaTournamentsRepository.retrieveTournamentResults(tournamentId)
}
