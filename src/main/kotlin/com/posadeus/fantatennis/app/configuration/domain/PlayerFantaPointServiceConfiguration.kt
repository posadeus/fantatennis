package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.service.player.PlayerFantaPointCalculatorService
import com.posadeus.fantatennis.domain.service.player.PlayerFantaPointPersistenceService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PlayerFantaPointServiceConfiguration {

  @Bean
  open fun playerFantaPointCalculatorService(tennisTvTournamentInfoRepository: TournamentInfoRepository,
                                             mySqlTournamentsRepository: TournamentsRepository): PlayerFantaPointCalculatorService =
      PlayerFantaPointCalculatorService(tennisTvTournamentInfoRepository,
                                        mySqlTournamentsRepository)

  @Bean
  open fun playerFantaPointPersistenceService(mySqlPlayerPointsRepository: PlayerPointsRepository): PlayerFantaPointPersistenceService =
      PlayerFantaPointPersistenceService(mySqlPlayerPointsRepository)
}