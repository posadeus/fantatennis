package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.TeamsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.*
import com.posadeus.fantatennis.domain.model.AddPlayers.ValidAddPlayers
import com.posadeus.fantatennis.domain.model.Swap.SwapCompleted
import com.posadeus.fantatennis.domain.model.Swap.SwapFailed
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*
import kotlin.jvm.optionals.getOrNull

class MySqlTeamsRepository(private val fantaTeamsDao: FantaTeamsDao,
                           private val tournamentsDao: TournamentsDao,
                           private val playersDao: PlayersDao,
                           private val teamsDao: TeamsDao) : TeamsRepository {

  override fun addPlayers(teamId: Int, playerIds: Set<String>, startingTournamentId: Int): AddPlayers {

    try {

      val fantaTeam = fantaTeamsDao.findById(teamId).getOrNull()
                      ?: return AddPlayersTeamNotFound

      val startingTournament = tournamentsDao.findById(startingTournamentId).getOrNull()
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

  // TODO Implement IT for this in MySqlTeamsRepositoryIT
  override fun swapPlayers(swapCommand: SwapCommand): Swap {

    val teamsEntities = swapCommand.playersToRemove
        .map { TeamsKeyEmbedded(swapCommand.teamId, it) }
        .let { teamsDao.findAllById(it) }

    if (teamsEntities.count() != swapCommand.playersToRemove.size)
      return SwapFailed

    val tournamentsEntities = listOf(swapCommand.endingTournament, swapCommand.startingTournament)
        .let { tournamentsDao.findAllById(it) }

    if (tournamentsEntities.count() != 2)
      return SwapFailed

    val playersEntities = playersDao.findAllById(swapCommand.playersToAdd)

    if (playersEntities.count() != swapCommand.playersToAdd.size)
      return SwapFailed

    val endingTournamentEntity = tournamentsEntities.first { it.id == swapCommand.endingTournament }
    val startingTournamentEntity = tournamentsEntities.first { it.id == swapCommand.startingTournament }

    val entitiesToUpdate = teamsEntities
        .map { it.copy(endingTournament = endingTournamentEntity) }

    val fantaTeamEntity = teamsEntities.first().fantaTeam

    val entitiesToAdd = playersEntities
        .map { toTeamsEntity(swapCommand.teamId, it, fantaTeamEntity, startingTournamentEntity) }

    val entities = entitiesToUpdate union entitiesToAdd

    teamsDao.saveAll(entities)

    return SwapCompleted
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
