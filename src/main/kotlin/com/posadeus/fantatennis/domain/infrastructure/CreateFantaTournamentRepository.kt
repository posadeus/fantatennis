package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentToCreateDto
import com.posadeus.fantatennis.domain.model.FantaTournament

interface CreateFantaTournamentRepository {

  fun create(dto: FantaTournamentToCreateDto): FantaTournament
}