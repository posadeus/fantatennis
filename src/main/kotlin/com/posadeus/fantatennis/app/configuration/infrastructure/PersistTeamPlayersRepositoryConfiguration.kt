package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.domain.infrastructure.PersistTeamPlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.SqlPersistTeamPlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TeamDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PersistTeamPlayersRepositoryConfiguration {

  @Bean
  fun sqlPersistTeamPlayersRepository(jdbcTeamDao: TeamDao): PersistTeamPlayersRepository =
      SqlPersistTeamPlayersRepository(jdbcTeamDao)
}