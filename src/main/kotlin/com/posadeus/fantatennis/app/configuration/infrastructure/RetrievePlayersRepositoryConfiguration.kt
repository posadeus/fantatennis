package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.SqlRetrievePlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RetrievePlayersRepositoryConfiguration {

  @Bean
  fun sqlRetrievePlayersRepository(cachedPlayerDao: PlayerDao): RetrievePlayersRepository =
      SqlRetrievePlayersRepository(cachedPlayerDao)
}