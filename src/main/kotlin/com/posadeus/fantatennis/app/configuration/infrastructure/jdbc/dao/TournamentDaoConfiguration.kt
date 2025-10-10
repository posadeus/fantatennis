package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament.CachedTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament.JdbcTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
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

  // TODO Take the configuration from the yml and choose the correct values
  @Bean
  fun tournamentCache(): Cache<Int, List<JdbcTournamentDto>> =
      Caffeine.newBuilder()
          .expireAfterWrite(1440, TimeUnit.MINUTES)
          .maximumSize(300)
          .build()
}