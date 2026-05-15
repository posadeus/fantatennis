package com.posadeus.fantatennis.infrastructure.repository.database

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.InvalidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournaments
import com.posadeus.fantatennis.domain.model.FantaTournaments.Invalid
import com.posadeus.fantatennis.domain.model.FantaTournaments.Valid
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException

class SqlRetrieveFantaTournamentsRepository(private val fantaTournamentDao: FantaTournamentDao) : RetrieveFantaTournamentsRepository {

  override fun retrieve(): FantaTournaments =
      try {

        fantaTournamentDao.retrieveAll()
            .map(::toValidFantaTournament)
            .let(List<ValidFantaTournament>::toSet)
            .let(::Valid)
      }
      catch (e: Exception) {

        LOGGER.error("Error retrieving fanta tournaments", e)
        Invalid
      }

  override fun retrieveBy(fantaTournamentId: Int): FantaTournament =
      try {

        fantaTournamentDao.retrieveBy(fantaTournamentId)
            .let(::toValidFantaTournament)
      }
      catch (_: EmptyResultDataAccessException) {

        InvalidFantaTournament
      }
      catch (e: Exception) {

        LOGGER.error("Error retrieving fanta tournament $fantaTournamentId", e)
        InvalidFantaTournament
      }

  private fun toValidFantaTournament(dto: JdbcFantaTournamentDto): ValidFantaTournament =
      ValidFantaTournament(id = dto.id,
                           startingTournamentId = dto.startingTournamentId,
                           endingTournamentId = dto.endingTournamentId,
                           tournamentYear = dto.year)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(SqlRetrieveFantaTournamentsRepository::class.java)
  }
}
