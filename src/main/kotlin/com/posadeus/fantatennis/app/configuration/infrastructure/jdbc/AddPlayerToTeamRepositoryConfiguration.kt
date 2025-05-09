package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.AddPlayersToTeamRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcAddPlayersToTeamRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class AddPlayerToTeamRepositoryConfiguration {

  @Bean
  fun jdbcAddPlayerToTeamRepository(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): AddPlayersToTeamRepository =
      JdbcAddPlayersToTeamRepository(namedParameterJdbcTemplate)
}