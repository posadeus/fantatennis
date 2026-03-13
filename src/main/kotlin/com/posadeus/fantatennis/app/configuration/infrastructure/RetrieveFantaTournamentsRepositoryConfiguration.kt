package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.SqlRetrieveFantaTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RetrieveFantaTournamentsRepositoryConfiguration {

  @Bean
  fun sqlRetrieveFantaTournamentsRepository(cachedFantaTournamentDao: FantaTournamentDao): RetrieveFantaTournamentsRepository =
      SqlRetrieveFantaTournamentsRepository(cachedFantaTournamentDao)
}
