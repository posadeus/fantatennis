package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.domain.infrastructure.RetrieveTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.SqlRetrieveTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RetrieveTournamentsRepositoryConfiguration {

  @Bean
  fun sqlRetrieveTournamentsRepository(cachedTournamentDao: TournamentDao): RetrieveTournamentsRepository =
      SqlRetrieveTournamentsRepository(cachedTournamentDao)
}