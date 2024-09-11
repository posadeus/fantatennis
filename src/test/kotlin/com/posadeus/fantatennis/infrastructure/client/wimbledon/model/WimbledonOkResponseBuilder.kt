package com.posadeus.fantatennis.infrastructure.client.wimbledon.model

import com.posadeus.fantatennis.infrastructure.client.wimbledon.model.WimbledonMatchBuilder.Companion.aWimbledonMatch

class WimbledonOkResponseBuilder(private var eventName: Any? = null,
                                 private var drawSize: Any? = null,
                                 private var drawFormat: Any? = null,
                                 private var totalRounds: Any? = null,
                                 private var prizeMoney: Any? = null,
                                 private var matches: Array<WimbledonMatch> = arrayOf(aWimbledonMatch().build())) {

  fun withMatches(matches: Array<WimbledonMatch>): WimbledonOkResponseBuilder {
    this.matches = matches
    return this
  }

  fun build() = WimbledonOkResponse(eventName,
                                    drawSize,
                                    drawFormat,
                                    totalRounds,
                                    prizeMoney,
                                    matches)

  companion object {

    fun aWimbledonOkResponse() =
        WimbledonOkResponseBuilder()
  }
}