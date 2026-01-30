package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournament.CachedFantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournament.JdbcFantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.concurrent.TimeUnit.MINUTES

@Configuration
class FantaTournamentDaoConfiguration {

  @Bean
  fun jdbcFantaTournamentDao(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): FantaTournamentDao =
      JdbcFantaTournamentDao(namedParameterJdbcTemplate)

  @Bean
  fun cachedFantaTournamentDao(fantaTournamentCache: Cache<Int, JdbcFantaTournamentDto>,
                               jdbcFantaTournamentDao: FantaTournamentDao): FantaTournamentDao =
      CachedFantaTournamentDao(fantaTournamentCache,
                               jdbcFantaTournamentDao)

  @Bean
  fun fantaTournamentCache(
      @Value("\${caches.caffeine.fanta-tournament-cache.expire-after-write-duration}") expireAfterWriteDuration: Long,
      @Value("\${caches.caffeine.fanta-tournament-cache.expire-after-access-duration}") expireAfterAccessDuration: Long,
      @Value("\${caches.caffeine.fanta-tournament-cache.maximum-size}") maximumSize: Long
  ): Cache<Int, JdbcFantaTournamentDto> =
      Caffeine.newBuilder()
          .expireAfterWrite(expireAfterWriteDuration, MINUTES)
          .expireAfterAccess(expireAfterAccessDuration, MINUTES)
          .maximumSize(maximumSize)
          .build()
}