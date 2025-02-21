package com.posadeus.fantatennis.infrastructure.client.tennistv

import com.posadeus.fantatennis.infrastructure.client.tennistv.model.TennisTvTournamentResponse
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.TennisTvTournamentsRegistryResponse

interface TennisTvClient {

  fun retrieveTournamentInfo(tournamentId: Int, year: Int): TennisTvTournamentResponse
  fun retrieveTournamentsRegistry(year: Int): TennisTvTournamentsRegistryResponse
}
