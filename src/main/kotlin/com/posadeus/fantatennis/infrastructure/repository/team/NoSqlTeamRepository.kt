package com.posadeus.fantatennis.infrastructure.repository.team

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.TeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.client.firebase.FirebaseClient
import com.posadeus.fantatennis.infrastructure.client.firebase.model.TeamOkResponse

class NoSqlTeamRepository(private val client: FirebaseClient) : TeamRepository {

  override fun getTeam(userId: Long, teamId: String): Team =
      convert(client.retrieveTeam(userId, teamId))

  private fun convert(team: TeamOkResponse): Team =
      team.players
          ?.map { TeamPlayerDto(fullName = "${it.firstName} ${it.lastname}",
                                fantaPoints = it.fantaPoints,
                                chosen = it.chosen,
                                playing = it.playing)
          }
          ?.let { FoundTeam(TeamDto(it)) }
      ?: ErrorTeam // FIXME EmptyTeam could be an option
}
