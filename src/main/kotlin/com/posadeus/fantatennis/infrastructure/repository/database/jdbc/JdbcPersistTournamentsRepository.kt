package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.exception.InvalidTournamentException
import com.posadeus.fantatennis.domain.infrastructure.PersistTournamentsRepository
import com.posadeus.fantatennis.domain.model.TournamentRegistry
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.FoundTournamentsRegistry
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.NewJdbcTournamentDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class JdbcPersistTournamentsRepository(private val tournamentDao: TournamentDao) : PersistTournamentsRepository {

  override fun persistNewTournaments(tournaments: FoundTournamentsRegistry) {
    try {

      tournaments.tournaments
          .map(::toJdbcTournamentDto)
          .let(tournamentDao::persistNewTournaments)
    }
    catch (e: RuntimeException) {

      throw InvalidTournamentException(error = "${e.message}")
    }
  }

  private fun toJdbcTournamentDto(tournamentRegistry: TournamentRegistry): NewJdbcTournamentDto =
      NewJdbcTournamentDto(atpTourId = tournamentRegistry.atpTourId,
                           tennisTvId = tournamentRegistry.tennisTvId,
                           name = tournamentRegistry.name,
                           points = tournamentRegistry.points,
                           location = tournamentRegistry.location,
                           surface = tournamentRegistry.surface.name,
                           year = tournamentRegistry.year,
                           startDate = tournamentRegistry.startDate.let(::toLocalDate),
                           endDate = tournamentRegistry.endDate.let(::toLocalDate))

  private fun toLocalDate(date: String): LocalDate =
      LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
}
