package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament.CachedTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament.JdbcTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import java.util.concurrent.TimeUnit

@Configuration
class TournamentDaoConfiguration {

  @Bean
  fun jdbcTournamentDao(jdbcTemplate: JdbcTemplate): TournamentDao =
      JdbcTournamentDao(jdbcTemplate)

  @Bean
  fun cachedTournamentDao(tournamentCache: Cache<Int, List<JdbcTournamentDto>>,
                          jdbcTournamentDao: TournamentDao): TournamentDao =
      CachedTournamentDao(tournamentCache,
                          jdbcTournamentDao)

  @Bean
  fun tournamentCache(
      @Value("\${caches.caffeine.tournament-cache.expire-after-write-duration}") expireAfterWriteDuration: Long,
      @Value("\${caches.caffeine.tournament-cache.expire-after-access-duration}") expireAfterAccessDuration: Long,
      @Value("\${caches.caffeine.tournament-cache.maximum-size}") maximumSize: Long
  ): Cache<Int, List<JdbcTournamentDto>> =
      Caffeine.newBuilder()
          .expireAfterWrite(expireAfterWriteDuration, TimeUnit.MINUTES)
          .expireAfterAccess(expireAfterAccessDuration, TimeUnit.MINUTES)
          .maximumSize(maximumSize)
          .build()
}