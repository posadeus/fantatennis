package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.PersistTeamPlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcPersistTeamPlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TeamDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PersistTeamPlayersRepositoryConfiguration {

  @Bean
  fun jdbcPersistTeamPlayersRepository(jdbcTeamDao: TeamDao): PersistTeamPlayersRepository =
      JdbcPersistTeamPlayersRepository(jdbcTeamDao)
}