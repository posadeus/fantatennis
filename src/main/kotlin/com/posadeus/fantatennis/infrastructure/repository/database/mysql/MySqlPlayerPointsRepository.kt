package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayersPointsEmbedded
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayersPointsEntity

class MySqlPlayerPointsRepository(private val playersPointsDao: PlayersPointsDao): PlayerPointsRepository {

  override fun save(players: Set<DomainPlayer>) {

    playersPointsDao.saveAll(convert(players))
  }

  private fun convert(players: Set<DomainPlayer>): Set<PlayersPointsEntity> =
      players.flatMap { player ->
        val playerId = player.id
        player.tournamentPoints.flatMap { tournament ->
          val year = tournament.key
          tournament.value.map {
            PlayersPointsEntity(PlayersPointsEmbedded(year, it.key, playerId), it.value)
          }
        }
      }.toSet()
}
