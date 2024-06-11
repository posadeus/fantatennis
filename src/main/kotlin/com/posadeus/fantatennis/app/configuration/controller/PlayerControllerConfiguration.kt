package com.posadeus.fantatennis.app.configuration.controller

import com.posadeus.fantatennis.controller.PlayerApi
import com.posadeus.fantatennis.controller.player.PlayerController
import com.posadeus.fantatennis.domain.service.player.PlayerService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PlayerControllerConfiguration {

  @Bean
  open fun playerApi(playerService: PlayerService): PlayerApi =
      PlayerController(playerService)
}