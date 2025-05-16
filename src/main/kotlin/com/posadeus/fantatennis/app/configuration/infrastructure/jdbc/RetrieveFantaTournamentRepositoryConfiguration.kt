package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrieveFantaTournamentRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class RetrieveFantaTournamentRepositoryConfiguration {

  @Bean
  fun jdbcRetrieveFantaTournamentRepository(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): RetrieveFantaTournamentRepository =
      JdbcRetrieveFantaTournamentRepository(namedParameterJdbcTemplate)
}