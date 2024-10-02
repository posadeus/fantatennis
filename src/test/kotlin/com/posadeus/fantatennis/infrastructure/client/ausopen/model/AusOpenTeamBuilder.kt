package com.posadeus.fantatennis.infrastructure.client.ausopen.model

class AusOpenTeamBuilder(private var team_id: String = "A_TEAM_ID",
                         private var score: Any? = null,
                         private var status: String? = null) {

  fun withTeamId(team_id: String): AusOpenTeamBuilder {
    this.team_id = team_id
    return this
  }

  fun withStatus(status: String?): AusOpenTeamBuilder {
    this.status = status
    return this
  }

  fun build() = AusOpenTeam(team_id,
                            score,
                            status)

  companion object {

    fun anAusOpenTeam() =
        AusOpenTeamBuilder()
  }
}