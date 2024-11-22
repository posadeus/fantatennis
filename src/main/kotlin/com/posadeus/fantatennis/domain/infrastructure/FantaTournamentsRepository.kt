package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.controller.model.tournament.TournamentToCreateDto
import com.posadeus.fantatennis.domain.model.FantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournamentResults

interface FantaTournamentsRepository {

  fun create(tournamentToCreate: TournamentToCreateDto): FantaTournament
  fun retrieve(tournamentId: Int): FantaTournament
  fun retrieveTournamentResults(tournamentId: Int): FantaTournamentResults
}
