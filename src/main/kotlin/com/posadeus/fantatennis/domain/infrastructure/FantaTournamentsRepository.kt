package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.FantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournamentResults

interface FantaTournamentsRepository {

  @Deprecated("To remove and use JdbcRetrieveFantaTournamentResultsRepository")
  fun retrieve(tournamentId: Int): FantaTournament
  fun retrieveTournamentResults(tournamentId: Int): FantaTournamentResults
  fun retrieveAll(): List<FantaTournament>
}
