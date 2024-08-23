package com.posadeus.fantatennis.infrastructure.client.tennistv

import com.posadeus.fantatennis.infrastructure.client.tennistv.model.TennisTvTournamentResponse

interface TennisTvClient {

  fun retrieveTournamentInfo(tournamentId: Int, year: Int): TennisTvTournamentResponse
}
