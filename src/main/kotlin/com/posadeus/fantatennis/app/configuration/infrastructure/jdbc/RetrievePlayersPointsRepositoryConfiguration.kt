package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrievePlayersPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints.JdbcPlayerPointsDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RetrievePlayersPointsRepositoryConfiguration {

  // TODO add CachedPlayerPointsDao
  @Bean
  fun jdbcRetrievePlayersPointsRepository(jdbcPlayerPointsDao: JdbcPlayerPointsDao): RetrievePlayersPointsRepository =
      JdbcRetrievePlayersPointsRepository(jdbcPlayerPointsDao)
}