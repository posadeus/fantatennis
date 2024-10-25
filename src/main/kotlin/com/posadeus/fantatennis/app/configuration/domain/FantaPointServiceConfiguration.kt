package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.service.player.*
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FantaPointServiceConfiguration {

  @Bean
  fun fantaPointCalculatorService(tournamentInfoRepositories: List<TournamentInfoRepository>,
                                       mySqlTournamentsRepository: TournamentsRepository): FantaPointCalculatorService =
      FantaPointCalculatorService(tournamentInfoRepositories,
                                  mySqlTournamentsRepository)

  @Bean
  fun fantaPointPersistenceService(mySqlPlayerPointsRepository: PlayerPointsRepository): FantaPointPersistenceService =
      FantaPointPersistenceService(mySqlPlayerPointsRepository)

  @Bean
  fun fantaPointService(fantaPointCalculatorService: FantaPointCalculatorService,
                             fantaPointPersistenceService: FantaPointPersistenceService,
                             playerService: PlayerService,
                             rankingService: RankingService): FantaPointService =
      FantaPointService(fantaPointCalculatorService,
                        fantaPointPersistenceService,
                        playerService,
                        rankingService)
}