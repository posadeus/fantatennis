package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.FantaTournamentsEntity
import org.slf4j.LoggerFactory

class MySqlFantaTournamentsRepository(private val fantaTournamentsDao: FantaTournamentsDao) : FantaTournamentsRepository {

  override fun retrieve(tournamentId: Int): FantaTournament =
      try {
        tournamentId
            .let(fantaTournamentsDao::findById)
            .map(::toValidFantaTournament)
            .orElse(InvalidFantaTournament)
      }
      catch (e: Exception) {

        LOGGER.error("Error during retrieve operation for tournament id: $tournamentId")
        InvalidFantaTournament
      }

  @Deprecated("Use JdbcRetrieveAllFantaTournamentsRepository::retrieve")
  override fun retrieveAll(): List<FantaTournament> =
      fantaTournamentsDao.findAll()
          .map(::toValidFantaTournament)

  private fun toValidFantaTournament(entity: FantaTournamentsEntity): FantaTournament =
      ValidFantaTournament(id = entity.id,
                           startingTournamentId = entity.startingTournament,
                           endingTournamentId = entity.endingTournament,
                           tournamentYear = entity.year)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(MySqlFantaTournamentsRepository::class.java)
  }
}

