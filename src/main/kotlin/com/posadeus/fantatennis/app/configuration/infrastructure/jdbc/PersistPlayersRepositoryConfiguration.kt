package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcPersistPlayersRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class PersistPlayersRepositoryConfiguration {

  @Bean
  fun jdbcPersistPlayersRepository(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): PersistPlayersRepository =
      JdbcPersistPlayersRepository(namedParameterJdbcTemplate)
}