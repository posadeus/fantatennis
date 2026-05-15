package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.infrastructure.repository.database.SqlRetrieveFantaTeamRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrieveFantaTeamRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class RetrieveFantaTeamRepositoryConfiguration {

  @Bean
  fun jdbcRetrieveFantaTeamRepository(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): RetrieveFantaTeamRepository =
      JdbcRetrieveFantaTeamRepository(namedParameterJdbcTemplate)

  @Bean
  fun sqlRetrieveFantaTeamRepository(cachedTeamDao: TeamDao,
                                     cachedFantaTeamDao: FantaTeamDao,
                                     cachedFantaTournamentTeamDao: FantaTournamentTeamDao): RetrieveFantaTeamRepository =
      SqlRetrieveFantaTeamRepository(cachedTeamDao,
                                     cachedFantaTeamDao,
                                     cachedFantaTournamentTeamDao)
}