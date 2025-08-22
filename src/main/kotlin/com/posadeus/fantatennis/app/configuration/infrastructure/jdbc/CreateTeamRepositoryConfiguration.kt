package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.CreateTeamRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcCreateTeamRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class CreateTeamRepositoryConfiguration {

  @Bean
  fun jdbcCreateTeamRepository(jdbcTemplate: JdbcTemplate,
                               namedParameterJdbcTemplate: NamedParameterJdbcTemplate): CreateTeamRepository =
      JdbcCreateTeamRepository(jdbcTemplate,
                               namedParameterJdbcTemplate)
}