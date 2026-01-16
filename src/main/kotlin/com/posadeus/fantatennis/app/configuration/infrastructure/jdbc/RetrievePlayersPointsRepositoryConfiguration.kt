package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrievePlayersPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints.JdbcPlayerPointsDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RetrievePlayersPointsRepositoryConfiguration {

  @Bean
  fun jdbcRetrievePlayersPointsRepository(jdbcPlayerPointsDao: JdbcPlayerPointsDao, // TODO use CachedPlayerPointsDao
                                          cachedPlayerDao: PlayerDao): RetrievePlayersPointsRepository =
      JdbcRetrievePlayersPointsRepository(jdbcPlayerPointsDao,
                                          cachedPlayerDao)
}