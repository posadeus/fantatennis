package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrievePlayersRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class RetrievePlayersRepositoryConfiguration {

  @Bean
  fun jdbcRetrievePlayersRepository(jdbcTemplate: JdbcTemplate): RetrievePlayersRepository =
      JdbcRetrievePlayersRepository(jdbcTemplate)
}