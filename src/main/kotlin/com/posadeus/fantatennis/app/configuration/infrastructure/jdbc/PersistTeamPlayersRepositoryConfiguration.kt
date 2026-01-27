package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.PersistTeamPlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcPersistTeamPlayersRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class PersistTeamPlayersRepositoryConfiguration {

  @Bean
  fun jdbcPersistTeamPlayersRepository(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): PersistTeamPlayersRepository =
      JdbcPersistTeamPlayersRepository(namedParameterJdbcTemplate)
}