package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.TournamentRepository
import com.posadeus.fantatennis.domain.service.player.PlayerFantaPointService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PlayerFantaPointServiceConfiguration {

  @Bean
  open fun playerFantaPointService(tennisTvTournamentRepository: TournamentRepository): PlayerFantaPointService =
      PlayerFantaPointService(tennisTvTournamentRepository)
}