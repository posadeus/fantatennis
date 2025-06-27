package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.PersistTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcPersistTournamentsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class PersistTournamentsRepositoryConfiguration {

  @Bean
  fun jdbcPersistTournamentsRepository(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): PersistTournamentsRepository =
      JdbcPersistTournamentsRepository(namedParameterJdbcTemplate)
}