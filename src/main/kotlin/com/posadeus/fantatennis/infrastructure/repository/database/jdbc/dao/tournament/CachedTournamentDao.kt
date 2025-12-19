package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto

class CachedTournamentDao(private val cache: Cache<Int, List<JdbcTournamentDto>>,
                          private val delegate: TournamentDao) : TournamentDao {

  override fun retrieveAllBy(year: Int): List<JdbcTournamentDto> =
      cache.get(year) { delegate.retrieveAllBy(year) }

  override fun retrieveBy(id: Int): JdbcTournamentDto {
    TODO("Not yet implemented")
  }
}
