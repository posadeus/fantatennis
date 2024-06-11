package com.posadeus.app.configuration.domain

import com.posadeus.domain.service.player.PlayerService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PlayerServiceConfiguration {

  @Bean
  open fun playerService(): PlayerService =
      PlayerService()
}