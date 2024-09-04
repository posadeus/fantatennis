package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.TeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.MySqlTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersDao
import org.slf4j.LoggerFactory

class MySqlTeamRepository(private val teamDao: MySqlTeamDao,
                          private val playerDao: PlayersDao) : TeamRepository {

  private val logger = LoggerFactory.getLogger(this::class.simpleName)

  override fun getTeam(teamId: String): Team {

    val teamEntities = teamDao.findByIdTeamId(teamId).associateBy { it.id.playerId }

    if (teamEntities.isEmpty()) {

      logger.error("Team $teamId not found")
      return EmptyTeam
    }

    val players = playerDao.findAllById(teamEntities.keys)
        .map {
          TeamPlayerDto(fullName = it.fullName,
                        fantaPoints = it.fantaPoints,
                        chosen = teamEntities[it.id]!!.chosen)
        }

    if (players.isEmpty() || players.size != teamEntities.keys.size) {

      logger.error("Player.size: ${players.size}, expected size: ${teamEntities.keys.size}")
      return ErrorTeam
    }

    return FoundTeam(team = TeamDto(players = players,
                                    totalScore = players.sumOf { it.fantaPoints },
                                    completed = players.size == FULL_TEAM_MEMBER_NUMBER))
  }

  companion object {

    private const val FULL_TEAM_MEMBER_NUMBER = 8
  }
}
