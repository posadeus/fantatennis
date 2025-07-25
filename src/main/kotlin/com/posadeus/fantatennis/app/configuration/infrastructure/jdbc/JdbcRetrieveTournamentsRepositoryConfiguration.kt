package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrieveTournamentsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class JdbcRetrieveTournamentsRepositoryConfiguration {

  @Bean
  fun jdbcRetrieveTournamentsRepository(jdbcTemplate: JdbcTemplate): RetrieveTournamentsRepository =
      JdbcRetrieveTournamentsRepository(jdbcTemplate)
}