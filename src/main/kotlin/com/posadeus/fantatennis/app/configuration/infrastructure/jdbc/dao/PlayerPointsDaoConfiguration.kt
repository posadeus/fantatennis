package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints.CachedPlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints.JdbcPlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.concurrent.TimeUnit

@Configuration
class PlayerPointsDaoConfiguration {

  @Bean
  fun jdbcPlayerPointsDao(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): PlayerPointsDao =
      JdbcPlayerPointsDao(namedParameterJdbcTemplate)

  @Bean
  fun cachedPlayerPointsDao(playersPointsByTournamentIdCache: Cache<Int, List<JdbcPlayerPointsDto>>,
                            playersPointsByTournamentYearCache: Cache<Int, List<JdbcPlayerPointsDto>>,
                            jdbcPlayerPointsDao: PlayerPointsDao): PlayerPointsDao =
      CachedPlayerPointsDao(playersPointsByTournamentIdCache,
                            playersPointsByTournamentYearCache,
                            jdbcPlayerPointsDao)

  @Bean
  fun playersPointsByTournamentIdCache(
      @Value("\${caches.caffeine.players-points-by-tournament-id-cache.expire-after-write-duration}") expireAfterWriteDuration: Long,
      @Value("\${caches.caffeine.players-points-by-tournament-id-cache.maximum-size}") maximumSize: Long
  ): Cache<Int, List<JdbcPlayerPointsDto>> =
      Caffeine.newBuilder()
          .expireAfterWrite(expireAfterWriteDuration, TimeUnit.MINUTES)
          .maximumSize(maximumSize)
          .build()

  @Bean
  fun playersPointsByTournamentYearCache(
      @Value("\${caches.caffeine.players-points-by-tournament-year-cache.expire-after-write-duration}") expireAfterWriteDuration: Long,
      @Value("\${caches.caffeine.players-points-by-tournament-year-cache.maximum-size}") maximumSize: Long
  ): Cache<Int, List<JdbcPlayerPointsDto>> =
      Caffeine.newBuilder()
          .expireAfterWrite(expireAfterWriteDuration, TimeUnit.MINUTES)
          .maximumSize(maximumSize)
          .build()
}