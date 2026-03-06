package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.domain.model.TeamId
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.team.CachedTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.team.JdbcTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.concurrent.TimeUnit

@Configuration
class TeamDaoConfiguration {

  @Bean
  fun jdbcTeamDao(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): TeamDao =
      JdbcTeamDao(namedParameterJdbcTemplate)

  @Bean
  fun cachedTeamDao(teamCache: Cache<Set<TeamId>, List<JdbcTeamDto>>,
                    jdbcTeamDao: TeamDao): TeamDao =
      CachedTeamDao(teamCache,
                    jdbcTeamDao)

  @Bean
  fun teamCache(
      @Value("\${caches.caffeine.team-cache.expire-after-write-duration}") expireAfterWriteDuration: Long,
      @Value("\${caches.caffeine.team-cache.maximum-size}") maximumSize: Long
  ): Cache<Set<TeamId>, List<JdbcTeamDto>> =
      Caffeine.newBuilder()
          .expireAfterWrite(expireAfterWriteDuration, TimeUnit.MINUTES)
          .maximumSize(maximumSize)
          .build()
}