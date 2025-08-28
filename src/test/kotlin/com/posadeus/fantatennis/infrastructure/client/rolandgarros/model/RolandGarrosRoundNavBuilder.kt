package com.posadeus.fantatennis.infrastructure.client.rolandgarros.model

class RolandGarrosRoundNavBuilder(private var label: String = "First Round",
                                  private var numberOfMatches: Int = 64) {

  fun withLabel(label: String): RolandGarrosRoundNavBuilder {
    this.label = label
    return this
  }

  fun withNumberOfMatches(numberOfMatches: Int): RolandGarrosRoundNavBuilder {
    this.numberOfMatches = numberOfMatches
    return this
  }

  fun build() = RolandGarrosRoundNav(label,
                                     numberOfMatches)

  companion object {

    fun aRolandGarrosRoundNav() =
        RolandGarrosRoundNavBuilder()
  }
}