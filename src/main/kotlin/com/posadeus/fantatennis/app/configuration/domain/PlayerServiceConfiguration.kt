package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.domain.service.player.PlayerService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PlayerServiceConfiguration {

  @Bean
  fun playerService(jdbcRetrievePlayersRepository: RetrievePlayersRepository,
                    jdbcPersistPlayersRepository: PersistPlayersRepository): PlayerService =
      PlayerService(jdbcRetrievePlayersRepository,
                    jdbcPersistPlayersRepository)
}