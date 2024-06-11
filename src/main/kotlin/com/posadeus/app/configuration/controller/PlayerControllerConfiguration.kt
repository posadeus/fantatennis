package com.posadeus.app.configuration.controller

import com.posadeus.controller.PlayerApi
import com.posadeus.controller.player.PlayerController
import com.posadeus.domain.service.player.PlayerService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PlayerControllerConfiguration {

  @Bean
  open fun playerApi(playerService: PlayerService): PlayerApi =
      PlayerController(playerService)
}