package com.posadeus.fantatennis.infrastructure.client.ausopen.model

import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenTeamBuilder.Companion.anAusOpenTeam

class AusOpenMatchBuilder(private var date: Any? = null,
                          private var actual_start_time: Any? = null,
                          private var match_centre_link: Any? = null,
                          private var uuid: Any? = null,
                          private var match_id: Any? = null,
                          private var team_substituted_footnote: Any? = null,
                          private var team_substituted: Any? = null,
                          private var id: Any? = null,
                          private var order: Any? = null,
                          private var promoted: Any? = null,
                          private var promoted_weight: Any? = null,
                          private var match_status: Any? = null,
                          private var match_state: Any? = null,
                          private var round_id: String = "A_ROUND_UUID",
                          private var duration: Any? = null,
                          private var teams: List<AusOpenTeam> = listOf(anAusOpenTeam().build()),
                          private var event_uuid: Any? = null,
                          private var court_id: Any? = null,
                          private var session: Any? = null,
                          private var session_order: Any? = null,
                          private var restricted_start_time: Any? = null,
                          private var restricted_start_time_timestamp: Any? = null,
                          private var activity_order: Any? = null) {

  fun withRoundId(round_id: String): AusOpenMatchBuilder {
    this.round_id = round_id
    return this
  }

  fun withTeams(teams: List<AusOpenTeam>): AusOpenMatchBuilder {
    this.teams = teams
    return this
  }

  fun build() = AusOpenMatch(date,
                             actual_start_time,
                             match_centre_link,
                             uuid,
                             match_id,
                             team_substituted_footnote,
                             team_substituted,
                             id,
                             order,
                             promoted,
                             promoted_weight,
                             match_status,
                             match_state,
                             round_id,
                             duration,
                             teams,
                             event_uuid,
                             court_id,
                             session,
                             session_order,
                             restricted_start_time,
                             restricted_start_time_timestamp,
                             activity_order)

  companion object {

    fun anAusOpenMatch() =
        AusOpenMatchBuilder()
  }
}