package com.posadeus.fantatennis.infrastructure.repository.firebase

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.TeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.client.firebase.model.*
import com.posadeus.fantatennis.infrastructure.client.firebase.team.FirebasePlayerDao
import com.posadeus.fantatennis.infrastructure.client.firebase.team.FirebaseTeamDao
import kotlinx.coroutines.runBlocking

class FirebaseTeamRepository(private val firebaseTeamDao: FirebaseTeamDao,
                             private val firebasePlayerDao: FirebasePlayerDao) : TeamRepository {

  override fun getTeam(teamId: String): Team =
      try {
        when (val team = getTeamFromRepository(teamId)) {
          is FoundFirebaseTeamResponse -> teamResponseFrom(team)
          is NotFoundFirebaseTeamResponse -> TeamIdNotFoundTeam
        }
      } catch (e: Exception) {
        // TODO add log
        ErrorTeam
      }

  private fun teamResponseFrom(team: FoundFirebaseTeamResponse) =
      if (team.players.isEmpty()) EmptyTeam
      else getAllPlayers().players
        .let { filterTeamPlayers(it, team) }
        .let { convertToFoundTeam(it, team) }

  private fun filterTeamPlayers(allPlayers: Map<String, FirebasePlayerResponse>,
                                team: FoundFirebaseTeamResponse): Map<String, FirebasePlayerResponse> =
      team.players
        .flatMap { player -> allPlayers.entries.filter { entry -> player.id == entry.key } }
        .associate { it.key to it.value }

  private fun convertToFoundTeam(teamPlayers: Map<String, FirebasePlayerResponse>,
                                 team: FoundFirebaseTeamResponse): FoundTeam =
      team.players
        .map {
          TeamPlayerDto(fullName = teamPlayers[it.id]!!.name,
                        fantaPoints = teamPlayers[it.id]!!.fantaPoints,
                        chosen = it.chosen)
        }
        .let {
          TeamDto(players = it,
                  totalScore = it.sumOf { player -> player.fantaPoints },
                  completed = it.size == TEAM_SIZE)
            .let(::FoundTeam)
        }

  private fun getTeamFromRepository(teamId: String): FirebaseTeamResponse =
      runBlocking { firebaseTeamDao.getTeam(teamId) }

  private fun getAllPlayers(): FirebasePlayersResponse =
      runBlocking { firebasePlayerDao.getAllPlayers() }

  companion object {

    private const val TEAM_SIZE = 8
  }
}
