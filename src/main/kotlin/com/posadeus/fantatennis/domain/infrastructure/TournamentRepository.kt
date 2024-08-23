package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.TournamentInfo

interface TournamentRepository {

  fun retrieveTournamentInfo(tournamentId: Int, year: Int): TournamentInfo
}
