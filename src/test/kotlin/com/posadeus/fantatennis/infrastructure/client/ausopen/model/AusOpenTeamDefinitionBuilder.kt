package com.posadeus.fantatennis.infrastructure.client.ausopen.model

class AusOpenTeamDefinitionBuilder(private var uuid: String = "A_TEAM_DEFINITION_UUID",
                                   private var seed: Any? = null,
                                   private var entry_status: Any? = null,
                                   private var players: List<String> = listOf("A_PLAYER_UUID")) {

  fun withUuid(uuid: String): AusOpenTeamDefinitionBuilder {
    this.uuid = uuid
    return this
  }

  fun withPlayers(players: List<String>): AusOpenTeamDefinitionBuilder {
    this.players = players
    return this
  }

  fun build() = AusOpenTeamDefinition(uuid,
                                      seed,
                                      entry_status,
                                      players)

  companion object {

    fun anAusOpenTeamDefinition() =
        AusOpenTeamDefinitionBuilder()
  }
}