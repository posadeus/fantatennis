package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.controller.model.tournament.TournamentToCreateDto
import com.posadeus.fantatennis.domain.model.FantaTournament

interface CreateFantaTournamentRepository {

  fun create(dto: TournamentToCreateDto): FantaTournament
}