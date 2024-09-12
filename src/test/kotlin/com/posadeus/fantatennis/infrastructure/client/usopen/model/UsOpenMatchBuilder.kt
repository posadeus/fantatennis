package com.posadeus.fantatennis.infrastructure.client.usopen.model

import com.posadeus.fantatennis.infrastructure.client.usopen.model.UsOpenTeamBuilder.Companion.aUsOpenTeam

class UsOpenMatchBuilder(private var match_id: Any? = null,
                         private var eventName: Any? = null,
                         private var shortEventName: Any? = null,
                         private var eventCode: Any? = null,
                         private var courtName: Any? = null,
                         private var shortCourtName: Any? = null,
                         private var courtId: Any? = null,
                         private var roundCode: Any? = null,
                         private var roundName: Any? = null,
                         private var roundNameShort: String = "1R",
                         private var eventDay: Any? = null,
                         private var duration: Any? = null,
                         private var statsLevel: Any? = null,
                         private var status: Any? = null,
                         private var statusCode: Any? = null,
                         private var winner: String? = "1",
                         private var epoch: Any? = null,
                         private var team1: UsOpenTeam = aUsOpenTeam().build(),
                         private var team2: UsOpenTeam = aUsOpenTeam().build(),
                         private var flags: Any? = null,
                         private var scores: Any? = null) {

  fun withRoundNameShort(roundNameShort: String): UsOpenMatchBuilder {
    this.roundNameShort = roundNameShort
    return this
  }

  fun withWinner(winner: String?): UsOpenMatchBuilder {
    this.winner = winner
    return this
  }

  fun withTeam1(team1: UsOpenTeam): UsOpenMatchBuilder {
    this.team1 = team1
    return this
  }

  fun withTeam2(team2: UsOpenTeam): UsOpenMatchBuilder {
    this.team2 = team2
    return this
  }

  fun build() = UsOpenMatch(match_id,
                            eventName,
                            shortEventName,
                            eventCode,
                            courtName,
                            shortCourtName,
                            courtId,
                            roundCode,
                            roundName,
                            roundNameShort,
                            eventDay,
                            duration,
                            statsLevel,
                            status,
                            statusCode,
                            winner,
                            epoch,
                            team1,
                            team2,
                            flags,
                            scores)

  companion object {

    fun aUsOpenMatch() =
        UsOpenMatchBuilder()
  }
}