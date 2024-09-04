package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.TournamentInfo

interface TournamentInfoRepository {

  fun retrieveTournamentInfo(tournamentId: Int, year: Int): TournamentInfo
}
