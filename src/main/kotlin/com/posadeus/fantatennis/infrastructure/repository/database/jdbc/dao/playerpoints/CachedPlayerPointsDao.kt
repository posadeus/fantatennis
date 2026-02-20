package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto

class CachedPlayerPointsDao(private val cacheById: Cache<Int, List<JdbcPlayerPointsDto>>,
                            private val cacheByYear: Cache<Int, List<JdbcPlayerPointsDto>>,
                            private val delegate: PlayerPointsDao) : PlayerPointsDao {

  override fun retrieveByTournamentId(tournamentId: Int): List<JdbcPlayerPointsDto> =
      cacheById.get(tournamentId) { delegate.retrieveByTournamentId(tournamentId) }

  override fun retrieveByTournamentYear(year: Int): List<JdbcPlayerPointsDto> =
      cacheByYear.get(year) { delegate.retrieveByTournamentYear(year) }

  override fun persistAll(players: List<JdbcPlayerPointsDto>) {

    delegate.persistAll(players)
    cacheById.invalidateAll()
    cacheByYear.invalidateAll()
  }
}
