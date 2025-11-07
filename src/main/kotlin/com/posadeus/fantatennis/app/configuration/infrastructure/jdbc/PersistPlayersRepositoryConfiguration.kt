package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcPersistPlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PersistPlayersRepositoryConfiguration {

  @Bean
  fun jdbcPersistPlayersRepository(cachedPlayerDao: PlayerDao): PersistPlayersRepository =
      JdbcPersistPlayersRepository(cachedPlayerDao)
}