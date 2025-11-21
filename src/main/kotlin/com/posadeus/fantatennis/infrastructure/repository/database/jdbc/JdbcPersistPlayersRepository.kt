package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.exception.InvalidPlayerException
import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.domain.model.PlayerPersistence
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceFailure
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceSuccess
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import org.slf4j.LoggerFactory

class JdbcPersistPlayersRepository(private val playerDao: PlayerDao) : PersistPlayersRepository {

  override fun persistAll(players: Set<DomainPlayer>): PlayerPersistence =
      try {

        players
            .map(::toJdbcPlayerDto)
            .toSet()
            .let(playerDao::persistAll)

        PlayerPersistenceSuccess
      }
      catch (e: InvalidPlayerException) {

        LOGGER.error("Invalid player exception: ${e.error}")

        PlayerPersistenceFailure(message = "Players $players not persisted.", error = e.error)
      }
      catch (e: RuntimeException) {

        LOGGER.error("Something went wrong during player persistence: ${e.message}")

        PlayerPersistenceFailure(message = "Persistence failure, please verify your input.", error = e.message)
      }

  private fun toJdbcPlayerDto(domainPlayer: DomainPlayer): JdbcPlayerDto =
      JdbcPlayerDto(playerId = domainPlayer.id,
                    atpTourId = domainPlayer.atpId,
                    fullName = domainPlayer.fullName,
                    rolandGarrosId = domainPlayer.rolandGarrosId)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(JdbcPersistPlayersRepository::class.java)
  }
}
