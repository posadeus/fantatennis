package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournamentteam

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentTeamDto

class CachedFantaTournamentTeamDao(private val cacheByTournamentId: Cache<Int, JdbcFantaTournamentTeamDto>,
                                   private val cacheByTeamId: Cache<Int, JdbcFantaTournamentTeamDto>,
                                   private val delegate: FantaTournamentTeamDao): FantaTournamentTeamDao {

  override fun persist(fantaTournamentTeam: JdbcFantaTournamentTeamDto) {

    delegate.persist(fantaTournamentTeam)
  }

  override fun retrieveBy(fantaTournamentId: Int): JdbcFantaTournamentTeamDto =
      cacheByTournamentId.get(fantaTournamentId) { delegate.retrieveBy(fantaTournamentId) }

  override fun retrieveByTeamId(teamId: Int): JdbcFantaTournamentTeamDto =
      cacheByTeamId.get(teamId) { delegate.retrieveByTeamId(teamId) }
}
