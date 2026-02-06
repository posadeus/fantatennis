package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournament

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto

class CachedFantaTournamentDao(private val fantaTournamentCache: Cache<Int, JdbcFantaTournamentDto>,
                               private val fantaTournamentsCache: Cache<Unit, List<JdbcFantaTournamentDto>>,
                               private val delegate: FantaTournamentDao) : FantaTournamentDao {

  override fun retrieveBy(fantaTournamentId: Int): JdbcFantaTournamentDto =
      fantaTournamentCache.get(fantaTournamentId) { delegate.retrieveBy(fantaTournamentId) }

  override fun retrieveAll(): List<JdbcFantaTournamentDto> =
      fantaTournamentsCache.get(Unit) { delegate.retrieveAll().takeIf { it.isNotEmpty() } }
      ?: emptyList()
}
