package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.TeamsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.TeamsEntity
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.TeamsKeyEmbedded

class MySqlTeamsRepository(private val fantaTeamsDao: FantaTeamsDao,
                           private val playersDao: PlayersDao,
                           private val teamsDao: TeamsDao) : TeamsRepository {

  override fun addPlayers(teamId: Int, playerIds: Set<String>): AddPlayers {

    val fantaTeam = fantaTeamsDao.findById(teamId)

    if (fantaTeam.isEmpty) return AddPlayersTeamNotFound

    val players = playersDao.findAllById(playerIds)

    val teamsEntities = players
        .map { TeamsEntity(id = TeamsKeyEmbedded(teamId = teamId, playerId = it.id),
                           player = it,
                           fantaTeam = fantaTeam.get()) }
        .toSet()

    teamsDao.saveAll(teamsEntities)

    return AddPlayersOk(players = players.map { DomainPlayer(id = it.id,
                                                             atpId = it.atpTourId,
                                                             fullName = it.fullName) }
        .toSet())
  }
}
