package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.team

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.domain.model.TeamId
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto

class CachedTeamDao(private val cache: Cache<Set<TeamId>, List<JdbcTeamDto>>,
                    private val delegate: TeamDao): TeamDao {

  override fun persist(teams: Set<JdbcTeamDto>) {

    delegate.persist(teams)

    teams
        .map { it.teamId }
        .toSet()
        .let { invalidateKeysContaining(cache, it) }
  }

  override fun retrieveBy(teamIds: Set<TeamId>): List<JdbcTeamDto> =
      cache.get(teamIds) { delegate.retrieveBy(teamIds) }

  companion object {

    fun invalidateKeysContaining(cache: Cache<Set<TeamId>, List<JdbcTeamDto>>, teamIds: Set<TeamId>) =
        cache.asMap().keys
            .filter { key -> key.any { it in teamIds } }
            .let(cache::invalidateAll)
  }
}
