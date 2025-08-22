package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentResultsRepository
import com.posadeus.fantatennis.domain.model.FantaTournamentResults

class RetrieveFantaTournamentService(private val retrieveFantaTournamentResultsRepository: RetrieveFantaTournamentResultsRepository) {

  fun retrieve(tournamentId: Int): FantaTournamentResults =
      retrieveFantaTournamentResultsRepository.retrieve(tournamentId)
}
