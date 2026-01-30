package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournament

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto

class CachedFantaTournamentDao(private val cache: Cache<Int, JdbcFantaTournamentDto>,
                               private val delegate: FantaTournamentDao) : FantaTournamentDao {

  override fun retrieveBy(fantaTournamentId: Int): JdbcFantaTournamentDto =
      cache.get(fantaTournamentId) { delegate.retrieveBy(fantaTournamentId) }
}
