package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.domain.infrastructure.CreateTeamRepository
import com.posadeus.fantatennis.infrastructure.repository.database.SqlCreateTeamRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CreateTeamRepositoryConfiguration {

  @Bean
  fun sqlCreateTeamRepository(cachedFantaTournamentDao: FantaTournamentDao,
                              cachedFantaTeamDao: FantaTeamDao,
                              jdbcFantaTournamentTeamDao: FantaTournamentTeamDao): CreateTeamRepository =
      SqlCreateTeamRepository(cachedFantaTournamentDao,
                              cachedFantaTeamDao,
                              jdbcFantaTournamentTeamDao)
}