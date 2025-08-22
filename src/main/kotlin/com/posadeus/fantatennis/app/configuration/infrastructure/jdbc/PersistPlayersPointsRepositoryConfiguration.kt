package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcPersistPlayersPointsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class PersistPlayersPointsRepositoryConfiguration {

  @Bean
  fun jdbcPersistPlayersPointsRepository(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): PersistPlayersPointsRepository =
      JdbcPersistPlayersPointsRepository(namedParameterJdbcTemplate)
}