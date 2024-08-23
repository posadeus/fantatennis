package com.posadeus.fantatennis.infrastructure.client.tennistv.model

class PlayerTeamBuilder(private var PlayerId: String = "PlayerId",
                        private var PartnerId: Any? = null,
                        private var PlayerFirstName: String? = null,
                        private var PlayerFirstNameFull: String? = null,
                        private var PlayerLastName: String? = null,
                        private var PlayerCountryCode: String? = null,
                        private var PartnerFirstName: String? = null,
                        private var PartnerFirstNameFull: Any? = null,
                        private var PartnerLastName: Any? = null,
                        private var PartnerCountryCode: Any? = null,
                        private var SeedPlayerTeam: Int? = null,
                        private var EntryStatusPlayerTeam: Any? = null,
                        private var GamePointsPlayerTeam: Any? = null,
                        private var Sets: Array<Any>? = null) {

  fun withPlayerId(PlayerId: String): PlayerTeamBuilder {
    this.PlayerId = PlayerId
    return this
  }

  fun build() = PlayerTeam(PlayerId,
                           PartnerId,
                           PlayerFirstName,
                           PlayerFirstNameFull,
                           PlayerLastName,
                           PlayerCountryCode,
                           PartnerFirstName,
                           PartnerFirstNameFull,
                           PartnerLastName,
                           PartnerCountryCode,
                           SeedPlayerTeam,
                           EntryStatusPlayerTeam,
                           GamePointsPlayerTeam,
                           Sets)

  companion object {

    fun aPlayerTeam() =
        PlayerTeamBuilder()
  }
}