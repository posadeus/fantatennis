package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.AddPlayersToTeamRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcAddPlayersToTeamRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class AddPlayersToTeamRepositoryConfiguration {

  @Bean
  fun jdbcAddPlayersToTeamRepository(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): AddPlayersToTeamRepository =
      JdbcAddPlayersToTeamRepository(namedParameterJdbcTemplate)
}