package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.team.JdbcTeamDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class TeamDaoConfiguration {

  @Bean
  fun jdbcTeamDao(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): TeamDao =
      JdbcTeamDao(namedParameterJdbcTemplate)
}