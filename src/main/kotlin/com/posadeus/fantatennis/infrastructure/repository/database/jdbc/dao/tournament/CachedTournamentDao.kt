package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.NewJdbcTournamentDto

class CachedTournamentDao(private val tournamentsCache: Cache<Int, List<JdbcTournamentDto>>,
                          private val tournamentCache: Cache<Int, JdbcTournamentDto>,
                          private val delegate: TournamentDao) : TournamentDao {

  override fun retrieveAllBy(year: Int): List<JdbcTournamentDto> =
      tournamentsCache.get(year) { delegate.retrieveAllBy(year) }

  override fun retrieveBy(id: Int): JdbcTournamentDto =
      tournamentCache.get(id) { delegate.retrieveBy(id) }

  override fun persistNewTournaments(tournaments: List<NewJdbcTournamentDto>) {

    delegate.persistNewTournaments(tournaments)

    tournaments
        .groupBy { it.year }
        .forEach { (year, _) -> tournamentsCache.invalidate(year) }
  }
}
