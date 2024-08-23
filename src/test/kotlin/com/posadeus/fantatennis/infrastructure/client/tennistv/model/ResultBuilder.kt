package com.posadeus.fantatennis.infrastructure.client.tennistv.model

import com.posadeus.fantatennis.infrastructure.client.tennistv.model.ResultTeamBuilder.Companion.aResultTeam

class ResultBuilder(private var MatchCode: Any? = null,
                    private var IsDoubles: Any? = null,
                    private var DrawLineTop: Any? = null,
                    private var DrawLineBottom: Any? = null,
                    private var ScoringType: Any? = null,
                    private var NumSets: Any? = null,
                    private var CourtSequence: Any? = null,
                    private var Day: Any? = null,
                    private var Winner: Any? = null,
                    private var ResultString: Any? = null,
                    private var ResultReason: Any? = null,
                    private var ResultType: Any? = null,
                    private var Umpire: Any? = null,
                    private var TeamTop: ResultTeam = aResultTeam().build(),
                    private var TeamBottom: ResultTeam = aResultTeam().build(),
                    private var SetResults: Any? = null,
                    private var MatchTime: Any? = null) {

  fun withTeamTop(TeamTop: ResultTeam): ResultBuilder {
    this.TeamTop = TeamTop
    return this
  }

  fun withTeamBottom(TeamBottom: ResultTeam): ResultBuilder {
    this.TeamBottom = TeamBottom
    return this
  }

  fun build() = Result(MatchCode,
                       IsDoubles,
                       DrawLineTop,
                       DrawLineBottom,
                       ScoringType,
                       NumSets,
                       CourtSequence,
                       Day,
                       Winner,
                       ResultString,
                       ResultReason,
                       ResultType,
                       Umpire,
                       TeamTop,
                       TeamBottom,
                       SetResults,
                       MatchTime)

  companion object {

    fun aResult() =
        ResultBuilder()
  }
}