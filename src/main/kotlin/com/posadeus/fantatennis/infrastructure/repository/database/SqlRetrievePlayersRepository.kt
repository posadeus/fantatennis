package com.posadeus.fantatennis.infrastructure.repository.database

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import org.slf4j.LoggerFactory

class SqlRetrievePlayersRepository(private val playerDao: PlayerDao) : RetrievePlayersRepository {

  override fun retrieve(): Set<DomainPlayer> =
      try {

        playerDao.retrieveAll()
            .map(::toDomainPlayer)
            .toSet()
      }
      catch (e: RuntimeException) {

        LOGGER.error("Error retrieving players", e)
        emptySet()
      }

  private fun toDomainPlayer(player: JdbcPlayerDto) =
      DomainPlayer(id = player.playerId,
                   atpId = player.atpTourId,
                   fullName = player.fullName,
                   rolandGarrosId = player.rolandGarrosId)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(SqlRetrievePlayersRepository::class.java)
  }
}
