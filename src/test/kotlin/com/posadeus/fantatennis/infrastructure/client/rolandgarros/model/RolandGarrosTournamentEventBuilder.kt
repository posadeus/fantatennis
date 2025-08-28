package com.posadeus.fantatennis.infrastructure.client.rolandgarros.model

import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosRoundNavBuilder.Companion.aRolandGarrosRoundNav
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosRoundResultBuilder.Companion.aRolandGarrosRoundResult

class RolandGarrosTournamentEventBuilder(private var activeRound: Any? = null,
                                         private var totalRounds: Any? = null,
                                         private var roundNavs: Array<RolandGarrosRoundNav> = arrayOf(aRolandGarrosRoundNav().build()),
                                         private var roundResults: Array<RolandGarrosRoundResult> = arrayOf(aRolandGarrosRoundResult().build())) {

  fun withRoundNavs(roundNavs: Array<RolandGarrosRoundNav>): RolandGarrosTournamentEventBuilder {
    this.roundNavs = roundNavs
    return this
  }

  fun withRoundResults(roundResults: Array<RolandGarrosRoundResult>): RolandGarrosTournamentEventBuilder {
    this.roundResults = roundResults
    return this
  }

  fun build() = RolandGarrosTournamentEvent(activeRound,
                                            totalRounds,
                                            roundNavs,
                                            roundResults)

  companion object {

    fun aRolandGarrosTournamentEvent() =
        RolandGarrosTournamentEventBuilder()
  }
}