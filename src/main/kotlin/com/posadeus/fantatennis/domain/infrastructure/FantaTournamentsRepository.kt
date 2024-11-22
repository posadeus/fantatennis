package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.controller.model.tournament.TournamentToCreateDto
import com.posadeus.fantatennis.domain.model.FantaTournament

interface FantaTournamentsRepository {

  fun retrieve(tournamentId: Int): FantaTournament
  fun create(tournamentToCreate: TournamentToCreateDto): FantaTournament
}
