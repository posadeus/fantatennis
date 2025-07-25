package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.PlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayersEntity

class MySqlPlayersRepository(private val playersDao: PlayersDao) : PlayersRepository {

  override fun saveAll(players: Set<DomainPlayer>) {

    players
        .map(::toPlayersEntity)
        .toSet()
        .let { playersDao.saveAll(it) }
  }

  private fun toPlayersEntity(domainPlayer: DomainPlayer) =
      PlayersEntity(id = domainPlayer.id,
                    atpTourId = domainPlayer.atpId,
                    fullName = domainPlayer.fullName)
}
