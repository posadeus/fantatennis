package com.posadeus.fantatennis.infrastructure.client.usopen.model

import com.posadeus.fantatennis.infrastructure.client.usopen.model.UsOpenMatchBuilder.Companion.aUsOpenMatch

class UsOpenOkResponseBuilder(private var eventName: Any? = null,
                              private var drawSize: Any? = null,
                              private var drawFormat: Any? = null,
                              private var totalRounds: Any? = null,
                              private var prizeMoney: Any? = null,
                              private var matches: Array<UsOpenMatch> = arrayOf(aUsOpenMatch().build())) {

  fun withMatches(matches: Array<UsOpenMatch>): UsOpenOkResponseBuilder {
    this.matches = matches
    return this
  }

  fun build() = UsOpenOkResponse(eventName,
                                 drawSize,
                                 drawFormat,
                                 totalRounds,
                                 prizeMoney,
                                 matches)

  companion object {

    fun aUsOpenOkResponse() =
        UsOpenOkResponseBuilder()
  }
}