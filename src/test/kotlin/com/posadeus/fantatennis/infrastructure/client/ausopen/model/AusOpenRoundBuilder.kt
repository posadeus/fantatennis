package com.posadeus.fantatennis.infrastructure.client.ausopen.model

class AusOpenRoundBuilder(private var uuid: String = "A_ROUND_UUID",
                          private var name: String = "A_ROUND_NAME") {

  fun withUuid(uuid: String): AusOpenRoundBuilder {
    this.uuid = uuid
    return this
  }

  fun withName(name: String): AusOpenRoundBuilder {
    this.name = name
    return this
  }

  fun build() = AusOpenRound(uuid,
                             name)

  companion object {

    fun anAusOpenRound() =
        AusOpenRoundBuilder()
  }
}