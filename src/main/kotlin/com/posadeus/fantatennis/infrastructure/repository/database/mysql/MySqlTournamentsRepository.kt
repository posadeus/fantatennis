package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.TournamentsRepository
import com.posadeus.fantatennis.domain.model.Tournament
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TournamentsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.TournamentsEntity

class MySqlTournamentsRepository(private val dao: TournamentsDao) : TournamentsRepository {

  override fun readTournaments(year: Int): List<Tournament> =
      year
          .let(dao::findByYear)
          .map(::convert)

  private fun convert(tournamentsEntities: TournamentsEntity): Tournament =
      Tournament(id = tournamentsEntities.id,
                 tennisTvId = tournamentsEntities.tennisTvId,
                 points = tournamentsEntities.points,
                 year = tournamentsEntities.year)
}
