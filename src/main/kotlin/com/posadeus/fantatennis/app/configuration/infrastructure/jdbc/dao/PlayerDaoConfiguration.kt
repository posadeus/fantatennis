package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.player.CachedPlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.player.JdbcPlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import java.util.concurrent.TimeUnit

@Configuration
class PlayerDaoConfiguration {

  @Bean
  fun jdbcPlayerDao(jdbcTemplate: JdbcTemplate): PlayerDao =
      JdbcPlayerDao(jdbcTemplate)

  @Bean
  fun cachedPlayerDao(playerCache: Cache<Unit, List<JdbcPlayerDto>>,
                      jdbcPlayerDao: PlayerDao): PlayerDao =
      CachedPlayerDao(playerCache,
                      jdbcPlayerDao)

  @Bean
  fun playerCache(
      @Value("\${caches.caffeine.player-cache.expire-after-write-duration}") expireAfterWriteDuration: Long,
      @Value("\${caches.caffeine.player-cache.maximum-size}") maximumSize: Long
  ): Cache<Unit, List<JdbcPlayerDto>> =
      Caffeine.newBuilder()
          .expireAfterWrite(expireAfterWriteDuration, TimeUnit.MINUTES)
          .maximumSize(maximumSize)
          .build()
}