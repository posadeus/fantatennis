package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveTournamentsRepository
import com.posadeus.fantatennis.domain.model.Tournament
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto

class JdbcRetrieveTournamentsRepository(private val tournamentDao: TournamentDao) : RetrieveTournamentsRepository {

  override fun retrieveAllBy(year: Int): List<Tournament> =
      tournamentDao.retrieveAllBy(year)
          .map(::toTournament)

  private fun toTournament(dto: JdbcTournamentDto): Tournament =
      Tournament(id = dto.tournamentId,
                 tennisTvId = dto.tennisTvId,
                 points = dto.points,
                 year = dto.year)
}
