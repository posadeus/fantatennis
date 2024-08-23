package com.posadeus.fantatennis.infrastructure.client.tennistv.model

class MatchRoundBuilder(private var ShortName: String = "ShortName",
                        private var LongName: String = "LongName") {

  fun withShortName(ShortName: String): MatchRoundBuilder {
    this.ShortName = ShortName
    return this
  }

  fun withLongName(LongName: String): MatchRoundBuilder {
    this.LongName = LongName
    return this
  }

  fun build() = MatchRound(ShortName,
                           LongName)

  companion object {

    fun aMatchRound() =
        MatchRoundBuilder()
  }
}