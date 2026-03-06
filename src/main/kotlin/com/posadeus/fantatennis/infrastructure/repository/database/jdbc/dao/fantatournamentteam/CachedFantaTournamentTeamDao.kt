package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournamentteam

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentTeamDto

class CachedFantaTournamentTeamDao(private val cacheByTournamentId: Cache<Int, List<JdbcFantaTournamentTeamDto>>,
                                   private val cacheByTeamId: Cache<Int, JdbcFantaTournamentTeamDto>,
                                   private val delegate: FantaTournamentTeamDao): FantaTournamentTeamDao {

  override fun persist(fantaTournamentTeam: JdbcFantaTournamentTeamDto) {

    delegate.persist(fantaTournamentTeam)
  }

  override fun retrieveByFantaTournamentId(id: Int): List<JdbcFantaTournamentTeamDto> =
      cacheByTournamentId.get(id) { delegate.retrieveByFantaTournamentId(id) }

  override fun retrieveByTeamId(id: Int): JdbcFantaTournamentTeamDto =
      cacheByTeamId.get(id) { delegate.retrieveByTeamId(id) }
}
