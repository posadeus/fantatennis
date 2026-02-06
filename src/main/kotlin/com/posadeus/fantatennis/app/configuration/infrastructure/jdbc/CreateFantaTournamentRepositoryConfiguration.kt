package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.CreateFantaTournamentRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcCreateFantaTournamentRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CreateFantaTournamentRepositoryConfiguration {

  @Bean
  fun jdbcCreateFantaTournamentRepository(cachedFantaTournamentDao: FantaTournamentDao): CreateFantaTournamentRepository =
      JdbcCreateFantaTournamentRepository(cachedFantaTournamentDao)
}