package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournamentteam.JdbcFantaTournamentTeamDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class FantaTournamentTeamDaoConfiguration {

  @Bean
  fun jdbcFantaTournamentTeamDao(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): FantaTournamentTeamDao =
      JdbcFantaTournamentTeamDao(namedParameterJdbcTemplate)
}