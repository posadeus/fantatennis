package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.player

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto

class CachedPlayerDao(private val cache: Cache<Unit, List<JdbcPlayerDto>>,
                      private val delegate: PlayerDao) : PlayerDao {

  override fun retrieveAll(): List<JdbcPlayerDto> =
      cache.get(Unit) { delegate.retrieveAll() }

  override fun persistAll(players: Set<JdbcPlayerDto>) {

    delegate.persistAll(players)
    cache.invalidate(Unit)
  }
}
