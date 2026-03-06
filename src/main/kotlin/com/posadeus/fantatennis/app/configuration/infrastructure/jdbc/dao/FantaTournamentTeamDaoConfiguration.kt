package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournamentteam.CachedFantaTournamentTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournamentteam.JdbcFantaTournamentTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentTeamDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.concurrent.TimeUnit

@Configuration
class FantaTournamentTeamDaoConfiguration {

  @Bean
  fun jdbcFantaTournamentTeamDao(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): FantaTournamentTeamDao =
      JdbcFantaTournamentTeamDao(namedParameterJdbcTemplate)

  @Bean
  fun cachedFantaTournamentTeamDao(fantaTournamentTeamCacheByTournamentId: Cache<Int, List<JdbcFantaTournamentTeamDto>>,
                                   fantaTournamentTeamCacheByTeamId: Cache<Int, JdbcFantaTournamentTeamDto>,
                                   jdbcFantaTournamentTeamDao: FantaTournamentTeamDao): FantaTournamentTeamDao =
      CachedFantaTournamentTeamDao(fantaTournamentTeamCacheByTournamentId,
                                   fantaTournamentTeamCacheByTeamId,
                                   jdbcFantaTournamentTeamDao)

  @Bean
  fun fantaTournamentTeamCacheByTournamentId(
      @Value("\${caches.caffeine.fanta-tournament-team-by-tournament-id-cache.expire-after-write-duration}") expireAfterWriteDuration: Long,
      @Value("\${caches.caffeine.fanta-tournament-team-by-tournament-id-cache.maximum-size}") maximumSize: Long
  ): Cache<Int, List<JdbcFantaTournamentTeamDto>> =
      Caffeine.newBuilder()
          .expireAfterWrite(expireAfterWriteDuration, TimeUnit.MINUTES)
          .maximumSize(maximumSize)
          .build()

  @Bean
  fun fantaTournamentTeamCacheByTeamId(
      @Value("\${caches.caffeine.fanta-tournament-team-by-team-id-cache.expire-after-write-duration}") expireAfterWriteDuration: Long,
      @Value("\${caches.caffeine.fanta-tournament-team-by-team-id-cache.maximum-size}") maximumSize: Long
  ): Cache<Int, JdbcFantaTournamentTeamDto> =
      Caffeine.newBuilder()
          .expireAfterWrite(expireAfterWriteDuration, TimeUnit.MINUTES)
          .maximumSize(maximumSize)
          .build()
}