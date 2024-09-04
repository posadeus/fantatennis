package com.posadeus.fantatennis.infrastructure.client.tennistv.model

class BreakdownBuilder(private var Id: Int? = null,
                       private var RoundIdModernized: Int? = null,
                       private var Name: String? = null,
                       private var PrizeMoney: String? = null,
                       private var Points: String = "1000") {

  fun build() = Breakdown(Id,
                          RoundIdModernized,
                          Name,
                          PrizeMoney,
                          Points)

  companion object {

    fun aBreakdown() =
        BreakdownBuilder()
  }
}