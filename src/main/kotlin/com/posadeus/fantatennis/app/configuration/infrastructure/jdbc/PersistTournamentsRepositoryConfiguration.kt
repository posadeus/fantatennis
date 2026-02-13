package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.PersistTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.SqlPersistTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament.CachedTournamentDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PersistTournamentsRepositoryConfiguration {

  @Bean
  fun sqlPersistTournamentsRepository(cachedTournamentDao: CachedTournamentDao): PersistTournamentsRepository =
      SqlPersistTournamentsRepository(cachedTournamentDao)
}