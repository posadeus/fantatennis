package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.TeamRepository
import com.posadeus.fantatennis.domain.model.FoundTeam
import com.posadeus.fantatennis.domain.model.Team

class MySqlTeamRepository(private val teamDao: MySqlTeamDao,
                          private val playerDao: MySqlPlayerDao) : TeamRepository {

  override fun getTeam(teamId: String): Team {

    val teamEntities = teamDao.findByIdTeamId(teamId).associateBy { it.id.playerId }
    val playerEntities = playerDao.findAllById(teamEntities.keys)

    val players = playerEntities
        .map {
          TeamPlayerDto(fullName = it.fullName,
                        fantaPoints = it.fantaPoints,
                        chosen = teamEntities[it.id]!!.chosen)
        }

    return FoundTeam(team = TeamDto(players = players,
                                    totalScore = players.sumOf { it.fantaPoints },
                                    completed = players.size == FULL_TEAM_MEMBER_NUMBER))
  }

  companion object {

    private const val FULL_TEAM_MEMBER_NUMBER = 8
  }
}
