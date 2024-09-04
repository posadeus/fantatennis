package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.TournamentsRepository
import com.posadeus.fantatennis.domain.model.Tournament
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TournamentsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.TournamentsEntity

class MySqlTournamentsRepository(private val dao: TournamentsDao) : TournamentsRepository {

  override fun readTournaments(): List<Tournament> =
      convert(dao.findAll())

  private fun convert(tournamentsEntities: Iterable<TournamentsEntity>): List<Tournament> =
      tournamentsEntities
          .map {
            Tournament(id = it.id,
                       atpTourId = it.atpTourId,
                       points = it.points)
          }
}
