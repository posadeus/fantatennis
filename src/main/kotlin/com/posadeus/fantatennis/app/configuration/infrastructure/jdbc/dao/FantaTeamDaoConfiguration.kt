package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantateam.CachedFantaTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantateam.JdbcFantaTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.concurrent.TimeUnit

@Configuration
class FantaTeamDaoConfiguration {

  @Bean
  fun jdbcFantaTeamDao(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): FantaTeamDao =
      JdbcFantaTeamDao(namedParameterJdbcTemplate)

  @Bean
  fun cachedFantaTeamDao(fantaTeamCache: Cache<Int, JdbcFantaTeamDto>,
                         jdbcFantaTeamDao: FantaTeamDao): FantaTeamDao =
      CachedFantaTeamDao(fantaTeamCache,
                         jdbcFantaTeamDao)

  @Bean
  fun fantaTeamCache(
      @Value("\${caches.caffeine.fanta-team-cache.expire-after-write-duration}") expireAfterWriteDuration: Long,
      @Value("\${caches.caffeine.fanta-team-cache.expire-after-access-duration}") expireAfterAccessDuration: Long,
      @Value("\${caches.caffeine.fanta-team-cache.maximum-size}") maximumSize: Long
  ): Cache<Int, JdbcFantaTeamDto> =
      Caffeine.newBuilder()
          .expireAfterWrite(expireAfterWriteDuration, TimeUnit.MINUTES)
          .expireAfterAccess(expireAfterAccessDuration, TimeUnit.MINUTES)
          .maximumSize(maximumSize)
          .build()
}