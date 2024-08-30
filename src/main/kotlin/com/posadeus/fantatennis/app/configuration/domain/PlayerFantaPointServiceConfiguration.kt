package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.infrastructure.TournamentRepository
import com.posadeus.fantatennis.domain.service.player.PlayerFantaPointCalculatorService
import com.posadeus.fantatennis.domain.service.player.PlayerFantaPointPersistenceService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PlayerFantaPointServiceConfiguration {

  @Bean
  open fun playerFantaPointCalculatorService(tennisTvTournamentRepository: TournamentRepository): PlayerFantaPointCalculatorService =
      PlayerFantaPointCalculatorService(tennisTvTournamentRepository)

  @Bean
  open fun playerFantaPointPersistenceService(mySqlPlayerPointsRepository: PlayerPointsRepository): PlayerFantaPointPersistenceService =
      PlayerFantaPointPersistenceService(mySqlPlayerPointsRepository)
}