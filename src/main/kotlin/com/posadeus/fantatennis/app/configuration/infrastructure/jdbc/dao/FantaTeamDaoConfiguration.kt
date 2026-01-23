package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantateam.JdbcFantaTeamDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class FantaTeamDaoConfiguration {

  @Bean
  fun jdbcFantaTeamDao(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): FantaTeamDao =
      JdbcFantaTeamDao(namedParameterJdbcTemplate)
}