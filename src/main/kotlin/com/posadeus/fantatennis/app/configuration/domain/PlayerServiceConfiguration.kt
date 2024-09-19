package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.PlayersRepository
import com.posadeus.fantatennis.domain.service.player.PlayerService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PlayerServiceConfiguration {

  @Bean
  open fun playerService(mySqlPlayersRepository: PlayersRepository): PlayerService =
      PlayerService(mySqlPlayersRepository)
}