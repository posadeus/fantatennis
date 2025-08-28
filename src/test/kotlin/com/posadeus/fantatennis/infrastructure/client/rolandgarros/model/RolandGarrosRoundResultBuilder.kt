package com.posadeus.fantatennis.infrastructure.client.rolandgarros.model

import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosMatchBuilder.Companion.aRolandGarrosMatch

class RolandGarrosRoundResultBuilder(private var roundLabel: String = "First Round",
                                     private var roundNumber: Int = 1,
                                     private var matches: Array<RolandGarrosMatch> = arrayOf(aRolandGarrosMatch().build())) {

  fun withRoundLabel(roundLabel: String): RolandGarrosRoundResultBuilder {
    this.roundLabel = roundLabel
    return this
  }

  fun withRoundNumber(roundNumber: Int): RolandGarrosRoundResultBuilder {
    this.roundNumber = roundNumber
    return this
  }

  fun withMatches(matches: Array<RolandGarrosMatch>): RolandGarrosRoundResultBuilder {
    this.matches = matches
    return this
  }

  fun build() = RolandGarrosRoundResult(roundLabel,
                                        roundNumber,
                                        matches)

  companion object {

    fun aRolandGarrosRoundResult() =
        RolandGarrosRoundResultBuilder()
  }
}