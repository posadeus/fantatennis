package com.posadeus.fantatennis.infrastructure.client.tennistv.model

import com.posadeus.fantatennis.infrastructure.client.tennistv.model.MSBuilder.Companion.aMS

class TournamentResponseBuilder(private var MS: MS = aMS().build(),
                                private var MD: Any? = null,
                                private var QS: Any? = null) {

  fun withMS(MS: MS): TournamentResponseBuilder {
    this.MS = MS
    return this
  }

  fun build() = TournamentResponse(MS,
                                   MD,
                                   QS)

  companion object {

    fun aTournamentResponse() =
        TournamentResponseBuilder()
  }
}