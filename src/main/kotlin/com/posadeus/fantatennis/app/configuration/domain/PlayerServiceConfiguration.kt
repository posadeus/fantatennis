package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.domain.service.player.PlayerService
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcPersistPlayersRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PlayerServiceConfiguration {

  @Bean
  fun playerService(jdbcRetrievePlayersRepository: RetrievePlayersRepository,
                    jdbcPersistPlayersRepository: JdbcPersistPlayersRepository): PlayerService =
      PlayerService(jdbcRetrievePlayersRepository,
                    jdbcPersistPlayersRepository)
}