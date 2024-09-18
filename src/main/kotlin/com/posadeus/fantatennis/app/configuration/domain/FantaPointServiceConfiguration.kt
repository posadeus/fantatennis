package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.service.PlayerFantaPointService
import com.posadeus.fantatennis.domain.service.player.FantaPointCalculatorService
import com.posadeus.fantatennis.domain.service.player.FantaPointPersistenceService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class FantaPointServiceConfiguration {

  @Bean
  open fun fantaPointCalculatorService(tournamentInfoRepositories: List<TournamentInfoRepository>,
                                       mySqlTournamentsRepository: TournamentsRepository): FantaPointCalculatorService =
      FantaPointCalculatorService(tournamentInfoRepositories,
                                  mySqlTournamentsRepository)

  @Bean
  open fun fantaPointPersistenceService(mySqlPlayerPointsRepository: PlayerPointsRepository): FantaPointPersistenceService =
      FantaPointPersistenceService(mySqlPlayerPointsRepository)

  @Bean
  open fun playerFantaPointService(fantaPointCalculatorService: FantaPointCalculatorService,
                                   fantaPointPersistenceService: FantaPointPersistenceService): PlayerFantaPointService =
      PlayerFantaPointService(fantaPointCalculatorService,
                              fantaPointPersistenceService)
}