package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrievePlayersPointsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RetrievePlayersPointsRepositoryConfiguration {

  @Bean
  fun jdbcRetrievePlayersPointsRepository(): RetrievePlayersPointsRepository =
      JdbcRetrievePlayersPointsRepository()
}