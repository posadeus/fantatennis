package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcPersistPlayersPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PersistPlayersPointsRepositoryConfiguration {

  @Bean
  fun jdbcPersistPlayersPointsRepository(cachedPlayerPointsDao: PlayerPointsDao): PersistPlayersPointsRepository =
      JdbcPersistPlayersPointsRepository(cachedPlayerPointsDao)
}