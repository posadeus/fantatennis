package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*

class MySqlPlayerPointsRepository(private val playersPointsDao: PlayersPointsDao) : PlayerPointsRepository {

  override fun save(players: Set<AtpPlayer>) {

    players
        .let(::toPlayersPointsEntities)
        .let { playersPointsDao.saveAll(it) }
  }

  private fun toPlayersPointsEntities(players: Set<AtpPlayer>): Set<PlayersPointsEntity> =
      players
          .flatMap { player ->

            val playerId = player.id

            player.tournamentPoints
                .flatMap { tournament ->

                  tournament.value.map { toPlayersPointsEntity(it, playerId, tournament.key) }
                }
          }
          .toSet()

  private fun toPlayersPointsEntity(pointsByTournamentId: Map.Entry<TournamentId, Double>,
                                    playerId: AtpPlayerId,
                                    tournamentYear: Year) =
      PlayersPointsEntity(id = PlayersPointsKeyEmbedded(tournamentYear = tournamentYear,
                                                        tournamentId = pointsByTournamentId.key,
                                                        playerId = playerId),
                          fantaPoints = pointsByTournamentId.value,
                          player = PlayersEntity(id = playerId),
                          tournament = TournamentsEntity(id = pointsByTournamentId.key))
}
