package com.posadeus.fantatennis.infrastructure.client.tennistv.model

import com.posadeus.fantatennis.infrastructure.client.tennistv.model.BreakdownBuilder.Companion.aBreakdown
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.RoundBuilder.Companion.aRound

class MSBuilder(private var EventTypeCode: String? = null,
                private var Description: String? = null,
                private var DrawSize: Int? = null,
                private var NumByes: Int? = null,
                private var HasRoundRobin: Boolean? = null,
                private var IsTeamEvent: Boolean? = null,
                private var Breakdown: Array<Breakdown> = arrayOf(aBreakdown().build()),
                private var RoundRobinRound: Any? = null,
                private var Rounds: Array<Round> = arrayOf(aRound().build()),
                private var Withdrawals: Any? = null) {

  fun withBreakdown(Breakdown: Array<Breakdown>): MSBuilder {
    this.Breakdown = Breakdown
    return this
  }

  fun withRounds(Rounds: Array<Round>): MSBuilder {
    this.Rounds = Rounds
    return this
  }

  fun build() = MS(EventTypeCode,
                   Description,
                   DrawSize,
                   NumByes,
                   HasRoundRobin,
                   IsTeamEvent,
                   Breakdown,
                   RoundRobinRound,
                   Rounds,
                   Withdrawals)

  companion object {

    fun aMS() =
        MSBuilder()
  }
}