package com.posadeus.fantatennis.infrastructure.repository.team

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.TeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.client.firebase.FirebaseClient
import com.posadeus.fantatennis.infrastructure.client.firebase.model.*

class NoSqlTeamRepository(private val client: FirebaseClient) : TeamRepository {

  override fun getTeam(userId: Long, teamId: String): Team =
      convert(client.retrieveTeam(userId, teamId))

  private fun convert(teamResponse: TeamResponse): Team =
      when (teamResponse) {

        is TeamOkResponse -> convertTeamOkResponse(teamResponse)
        is TeamNotFoundResponse -> TeamIdNotFoundTeam
      }

  private fun convertTeamOkResponse(teamResponse: TeamOkResponse) =
      (teamResponse.players
           ?.map {
             TeamPlayerDto(fullName = "${it.firstName} ${it.lastname}",
                           fantaPoints = it.fantaPoints,
                           chosen = it.chosen,
                           playing = it.playing)
           }
           ?.let {
             FoundTeam(TeamDto(players = it,
                               totalScore = it.sumOf { player -> player.fantaPoints },
                               completed = it.size == TEAM_SIZE))
           }
       ?: EmptyTeam)

  companion object {

    private const val TEAM_SIZE = 8
  }
}
