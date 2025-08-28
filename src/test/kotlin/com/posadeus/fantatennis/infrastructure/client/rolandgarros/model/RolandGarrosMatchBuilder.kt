package com.posadeus.fantatennis.infrastructure.client.rolandgarros.model

import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosMatchDataBuilder.Companion.aRolandGarrosMatchData
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosTeamBuilder.Companion.aRolandGarrosTeam

class RolandGarrosMatchBuilder(private var id: Any? = null,
                               private var url: Any? = null,
                               private var matchData: RolandGarrosMatchData = aRolandGarrosMatchData().build(),
                               private var teamA: RolandGarrosTeam = aRolandGarrosTeam().build(),
                               private var teamB: RolandGarrosTeam = aRolandGarrosTeam().build(),
                               private var umpire: Any? = null,
                               private var showUmpire: Any? = null,
                               private var excitementRate: Any? = null) {

  fun withTeamA(teamA: RolandGarrosTeam): RolandGarrosMatchBuilder {
    this.teamA = teamA
    return this
  }

  fun withTeamB(teamB: RolandGarrosTeam): RolandGarrosMatchBuilder {
    this.teamB = teamB
    return this
  }

  fun withMatchData(matchData: RolandGarrosMatchData): RolandGarrosMatchBuilder {
    this.matchData = matchData
    return this
  }

  fun build() = RolandGarrosMatch(id,
                                  url,
                                  matchData,
                                  teamA,
                                  teamB,
                                  umpire,
                                  showUmpire,
                                  excitementRate)

  companion object {

    fun aRolandGarrosMatch() =
        RolandGarrosMatchBuilder()
  }
}