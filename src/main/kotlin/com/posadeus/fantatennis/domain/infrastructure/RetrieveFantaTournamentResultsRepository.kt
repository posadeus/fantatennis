package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.FantaTournamentResults

interface RetrieveFantaTournamentResultsRepository {

  fun retrieve(tournamentId: Int): FantaTournamentResults
}
