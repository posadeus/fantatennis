package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.CreateFantaTournamentRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcCreateFantaTournamentRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class CreateFantaTournamentRepositoryConfiguration {

  @Bean
  fun jdbcCreateFantaTournamentRepository(jdbcTemplate: JdbcTemplate): CreateFantaTournamentRepository =
      JdbcCreateFantaTournamentRepository(jdbcTemplate)
}