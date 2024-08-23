package com.posadeus.fantatennis.infrastructure.client.tennistv.model

class ResultTeamPlayerBuilder(private var PlayerId: String = "PlayerId",
                              private var FirstName: Any? = null,
                              private var LastName: Any? = null,
                              private var Nationality: Any? = null,
                              private var OrderInTeam: Any? = null) {

  fun withPlayerId(PlayerId: String): ResultTeamPlayerBuilder {
    this.PlayerId = PlayerId
    return this
  }

  fun build() = ResultTeamPlayer(PlayerId,
                                 FirstName,
                                 LastName,
                                 Nationality,
                                 OrderInTeam)

  companion object {

    fun aResultTeamPlayer() =
        ResultTeamPlayerBuilder()
  }
}