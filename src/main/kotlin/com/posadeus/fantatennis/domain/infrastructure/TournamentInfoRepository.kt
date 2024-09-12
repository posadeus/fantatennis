package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.TournamentInfo

interface TournamentInfoRepository {

  fun canProcess(tournamentId: Int): Boolean
  fun retrieveTournamentInfo(tournamentId: Int, year: Int): TournamentInfo
}
