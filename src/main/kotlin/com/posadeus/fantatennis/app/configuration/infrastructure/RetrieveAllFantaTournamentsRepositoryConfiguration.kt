package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.domain.infrastructure.RetrieveAllFantaTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.SqlRetrieveAllFantaTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RetrieveAllFantaTournamentsRepositoryConfiguration {

  @Bean
  fun sqlRetrieveAllFantaTournamentsRepository(cachedFantaTournamentDao: FantaTournamentDao): RetrieveAllFantaTournamentsRepository =
      SqlRetrieveAllFantaTournamentsRepository(cachedFantaTournamentDao)
}