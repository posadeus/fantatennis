package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.TeamsRepository
import com.posadeus.fantatennis.domain.model.AddPlayers
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.*
import com.posadeus.fantatennis.domain.model.AddPlayers.ValidAddPlayers
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*
import kotlin.jvm.optionals.getOrNull

class MySqlTeamsRepository(private val fantaTeamsDao: FantaTeamsDao,
                           private val tournamentsDao: TournamentsDao,
                           private val playersDao: PlayersDao,
                           private val teamsDao: TeamsDao) : TeamsRepository {

  // TODO: Create a new endpoint to switch players
  override fun addPlayers(teamId: Int, playerIds: Set<String>, startingTournamentId: Int?): AddPlayers {

    try {

      val fantaTeam = fantaTeamsDao.findById(teamId).getOrNull()
                      ?: return AddPlayersTeamNotFound

      val startingTournament = tournamentsDao.findById(startingTournamentId!!).getOrNull()
                               ?: return AddPlayersTournamentNotFound

      val players = playersDao.findAllById(playerIds)

      if (players.count() != playerIds.size) {

        return PlayersNotFound(missingPlayerIds = missingPlayerIds(playerIds, players))
      }

      return players
          .map { toTeamsEntity(teamId, it, fantaTeam, startingTournament) }
          .toSet()
          .let(::persist)
          .map(::toDomainPlayer)
          .toSet()
          .let(::ValidAddPlayers)
    }
    catch (e: Exception) {

      return AddPlayersError
    }
  }

  private fun persist(it: Set<TeamsEntity>): Iterable<TeamsEntity> =
      teamsDao.saveAll(it)

  private fun missingPlayerIds(playerIds: Set<String>,
                               players: Iterable<PlayersEntity>) =
      playerIds
          .filter { id -> id !in players.map { it.id } }
          .toSet()

  private fun toDomainPlayer(teamsEntity: TeamsEntity) =
      DomainPlayer(id = teamsEntity.player.id,
                   atpId = teamsEntity.player.atpTourId,
                   fullName = teamsEntity.player.fullName)

  private fun toTeamsEntity(teamId: Int,
                            playersEntity: PlayersEntity,
                            fantaTeam: FantaTeamsEntity,
                            startingTournament: TournamentsEntity) =
      TeamsEntity(id = TeamsKeyEmbedded(teamId = teamId, playerId = playersEntity.id),
                  player = playersEntity,
                  fantaTeam = fantaTeam,
                  startingTournament = startingTournament,
                  endingTournament = null)
}
