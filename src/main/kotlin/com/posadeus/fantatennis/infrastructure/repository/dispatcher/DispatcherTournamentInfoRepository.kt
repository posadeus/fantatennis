package com.posadeus.fantatennis.infrastructure.repository.dispatcher

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.TournamentInfo
import com.posadeus.fantatennis.infrastructure.repository.tennistv.TennisTvTournamentInfoRepository
import com.posadeus.fantatennis.infrastructure.repository.wimbledon.WimbledonTournamentInfoRepository

class DispatcherTournamentInfoRepository(private val tennisTvRepository: TennisTvTournamentInfoRepository,
                                         private val wimbledonRepository: WimbledonTournamentInfoRepository)
  : TournamentInfoRepository {

  override fun retrieveTournamentInfo(tournamentId: Int, year: Int): TournamentInfo =
      when (tournamentId) {
        39 -> wimbledonRepository.retrieveTournamentInfo(tournamentId, year)
        else -> tennisTvRepository.retrieveTournamentInfo(tournamentId, year)
      }
}
