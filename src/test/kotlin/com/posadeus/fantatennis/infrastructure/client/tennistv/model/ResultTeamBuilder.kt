package com.posadeus.fantatennis.infrastructure.client.tennistv.model

import com.posadeus.fantatennis.infrastructure.client.tennistv.model.ResultTeamPlayerBuilder.Companion.aResultTeamPlayer

class ResultTeamBuilder(private var Player: ResultTeamPlayer? = aResultTeamPlayer().build(),
                        private var Partner: Any? = null) {

  fun withPlayer(Player: ResultTeamPlayer?): ResultTeamBuilder {
    this.Player = Player
    return this
  }

  fun build() = ResultTeam(Player,
                           Partner)

  companion object {

    fun aResultTeam() =
        ResultTeamBuilder()
  }
}