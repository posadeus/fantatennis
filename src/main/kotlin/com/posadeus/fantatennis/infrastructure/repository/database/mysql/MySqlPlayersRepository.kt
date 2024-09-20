package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.PlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayersEntity

class MySqlPlayersRepository(private val playersDao: PlayersDao) : PlayersRepository {

  override fun getAllPlayers(): Set<DomainPlayer> =
      convert(playersDao.findAll())

  override fun saveAll(players: Set<DomainPlayer>) {

    playersDao.saveAll(convert(players))
  }

  private fun convert(playersEntities: Iterable<PlayersEntity>): Set<DomainPlayer> =
      playersEntities
          .map {
            DomainPlayer(id = it.id,
                         atpId = it.atpTourId,
                         fullName = it.fullName)
          }
          .toSet()

  private fun convert(players: Set<DomainPlayer>): Set<PlayersEntity> =
      players
          .map {
            PlayersEntity(id = it.id,
                          atpTourId = it.atpId,
                          fullName = it.fullName)
          }
          .toSet()
}
