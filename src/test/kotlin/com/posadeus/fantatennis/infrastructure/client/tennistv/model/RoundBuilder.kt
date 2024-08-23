package com.posadeus.fantatennis.infrastructure.client.tennistv.model

import com.posadeus.fantatennis.infrastructure.client.tennistv.model.FixtureBuilder.Companion.aFixture

class RoundBuilder(private var RoundId: Int? = null,
                   private var RoundIdModernized: Int? = null,
                   private var RoundName: String = "Final",
                   private var Fixtures: Array<Fixture> = arrayOf(aFixture().build()),
                   private var TeamFixtures: Array<Any>? = null) {

  fun withRoundName(RoundName: String): RoundBuilder {
    this.RoundName = RoundName
    return this
  }

  fun withFixtures(Fixtures: Array<Fixture>): RoundBuilder {
    this.Fixtures = Fixtures
    return this
  }

  fun build() = Round(RoundId,
                      RoundIdModernized,
                      RoundName,
                      Fixtures,
                      TeamFixtures)

  companion object {

    fun aRound() =
        RoundBuilder()
  }
}