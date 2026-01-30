package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.CreateTeamRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcCreateTeamRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CreateTeamRepositoryConfiguration {

  @Bean
  fun jdbcCreateTeamRepository(cachedFantaTournamentDao: FantaTournamentDao,
                               cachedFantaTeamDao: FantaTeamDao,
                               jdbcFantaTournamentTeamDao: FantaTournamentTeamDao): CreateTeamRepository =
      JdbcCreateTeamRepository(cachedFantaTournamentDao,
                               cachedFantaTeamDao,
                               jdbcFantaTournamentTeamDao)
}