package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveTournamentsRepository
import com.posadeus.fantatennis.domain.model.Tournament
import com.posadeus.fantatennis.domain.model.Tournament.*
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException

class JdbcRetrieveTournamentsRepository(private val tournamentDao: TournamentDao) : RetrieveTournamentsRepository {

  override fun retrieveAllBy(year: Int): List<FoundTournament> =
      try {
        tournamentDao.retrieveAllBy(year)
            .map(::toTournament)
      }
      catch (e: RuntimeException) {

        LOGGER.error("Error during retrieve operation")
        emptyList()
      }

  override fun retrieveBy(tournamentId: Int): Tournament =
      try {
        tournamentDao.retrieveBy(tournamentId)
            .let(::toTournament)
      }
      catch (e: EmptyResultDataAccessException) {

        LOGGER.warn("Missing tournament with ID $tournamentId")
        NotFoundTournament
      }
      catch (e: RuntimeException) {

        LOGGER.error(e.message)
        InternalErrorTournament
      }

  private fun toTournament(dto: JdbcTournamentDto): FoundTournament =
      FoundTournament(id = dto.tournamentId,
                      tennisTvId = dto.tennisTvId,
                      name = dto.name,
                      points = dto.points,
                      year = dto.year)

  companion object {

    private val LOGGER = LoggerFactory.getLogger(JdbcRetrieveTournamentsRepository::class.java)
  }
}
