package com.posadeus.fantatennis.infrastructure.client.tennistv.model

import com.posadeus.fantatennis.infrastructure.client.tennistv.model.TournamentResponseBuilder.Companion.aTournamentResponse

class TennisTvTournamentOkResponseBuilder(private var tournament: TournamentResponse = aTournamentResponse().build()) {

  fun withTournament(tournament: TournamentResponse): TennisTvTournamentOkResponseBuilder {
    this.tournament = tournament
    return this
  }

  fun build() = TennisTvTournamentOkResponse(tournament)

  companion object {

    fun aTennisTvTournamentOkResponse() =
        TennisTvTournamentOkResponseBuilder()
  }
}
