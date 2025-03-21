package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.TournamentsRepository
import com.posadeus.fantatennis.domain.model.Tournament
import com.posadeus.fantatennis.domain.model.TournamentRegistry
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.FoundTournamentsRegistry
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TournamentsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.TournamentsEntity

class MySqlTournamentsRepository(private val dao: TournamentsDao) : TournamentsRepository {

  override fun readTournaments(year: Int): List<Tournament> =
      year
          .let(dao::findByYear)
          .map(::toTournament)

  override fun persist(tournamentsRegistry: FoundTournamentsRegistry) {

    tournamentsRegistry
        .tournaments
        .map(::toTournamentsEntity)
        .let(dao::saveAll)
  }

  override fun getAllTournaments(): List<Tournament> =
      dao.findAll()
         .map(::toTournament)

  private fun toTournamentsEntity(tournamentRegistry: TournamentRegistry): TournamentsEntity =
      TournamentsEntity(atpTourId = tournamentRegistry.atpTourId,
                        tennisTvId = tournamentRegistry.tennisTvId,
                        name = tournamentRegistry.name,
                        points = tournamentRegistry.points,
                        location = tournamentRegistry.location,
                        surface = tournamentRegistry.surface.name,
                        year = tournamentRegistry.year,
                        startDate = tournamentRegistry.startDate,
                        endDate = tournamentRegistry.endDate)

  private fun toTournament(tournamentsEntity: TournamentsEntity): Tournament =
      Tournament(id = tournamentsEntity.id,
                 tennisTvId = tournamentsEntity.tennisTvId,
                 points = tournamentsEntity.points,
                 year = tournamentsEntity.year)
}
