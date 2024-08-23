package com.posadeus.fantatennis.infrastructure.client.tennistv.model

import com.posadeus.fantatennis.infrastructure.client.tennistv.model.MatchBuilder.Companion.aMatch
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.ResultBuilder.Companion.aResult

class FixtureBuilder(private var MatchCode: String? = null,
                     private var Result: Result? = aResult().build(),
                     private var DrawLineTop: Any? = null,
                     private var DrawLineBottom: Any? = null,
                     private var TopPlaceholderText: String? = null,
                     private var BottomPlaceholderText: String? = null,
                     private var IsTopKnown: Boolean? = null,
                     private var IsBottomKnown: Boolean? = null,
                     private var Winner: Int? = null,
                     private var ResultString: String? = null,
                     private var PulseStatus: String? = null,
                     private var Match: Match? = aMatch().build()) {

  fun withResult(Result: Result?): FixtureBuilder {
    this.Result = Result
    return this
  }

  fun withMatch(Match: Match?): FixtureBuilder {
    this.Match = Match
    return this
  }

  fun build() = Fixture(MatchCode,
                        Result,
                        DrawLineTop,
                        DrawLineBottom,
                        TopPlaceholderText,
                        BottomPlaceholderText,
                        IsTopKnown,
                        IsBottomKnown,
                        Winner,
                        ResultString,
                        PulseStatus,
                        Match)

  companion object {

    fun aFixture() =
        FixtureBuilder()
  }
}