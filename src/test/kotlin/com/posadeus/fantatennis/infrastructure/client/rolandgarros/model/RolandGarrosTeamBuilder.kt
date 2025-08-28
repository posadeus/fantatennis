package com.posadeus.fantatennis.infrastructure.client.rolandgarros.model

import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosPlayerBuilder.Companion.aRolandGarrosPlayer

class RolandGarrosTeamBuilder(private var players: Array<RolandGarrosPlayer> = arrayOf(aRolandGarrosPlayer().build()),
                              private var sets: Any? = null,
                              private var winner: Boolean = false,
                              private var seed: Any? = null,
                              private var hasService: Any? = null,
                              private var points: Any? = null,
                              private var endCause: Any? = null,
                              private var entryStatus: Any? = null,
                              private var winnerOfLabel: Any? = null,
                              private var winnerOfPart1: Any? = null,
                              private var winnerOfPart2: Any? = null) {

  fun withPlayers(players: Array<RolandGarrosPlayer>): RolandGarrosTeamBuilder {
    this.players = players
    return this
  }

  fun withWinner(winner: Boolean): RolandGarrosTeamBuilder {
    this.winner = winner
    return this
  }

  fun build() = RolandGarrosTeam(players,
                                 sets,
                                 winner,
                                 seed,
                                 hasService,
                                 points,
                                 endCause,
                                 entryStatus,
                                 winnerOfLabel,
                                 winnerOfPart1,
                                 winnerOfPart2)

  companion object {

    fun aRolandGarrosTeam() =
        RolandGarrosTeamBuilder()
  }
}