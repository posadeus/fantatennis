package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantateam

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTeamId
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto

class CachedFantaTeamDao(private val cache: Cache<Int, JdbcFantaTeamDto>,
                         private val delegate: FantaTeamDao) : FantaTeamDao {

  override fun retrieveBy(teamId: Int): JdbcFantaTeamDto =
      cache.get(teamId) { delegate.retrieveBy(teamId) }

  override fun persist(ownerId: String): FantaTeamId =
      delegate.persist(ownerId)
}
