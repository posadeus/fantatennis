package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrieveTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RetrieveTournamentsRepositoryConfiguration {

  @Bean
  fun jdbcRetrieveTournamentsRepository(cachedTournamentDao: TournamentDao): RetrieveTournamentsRepository =
      JdbcRetrieveTournamentsRepository(cachedTournamentDao)
}