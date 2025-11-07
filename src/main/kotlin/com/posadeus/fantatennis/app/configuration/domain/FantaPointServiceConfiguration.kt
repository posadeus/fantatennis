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
                                  jdbcRetrieveTournamentsRepository: RetrieveTournamentsRepository): FantaPointCalculatorService =
      FantaPointCalculatorService(tournamentInfoRepositories,
                                  jdbcRetrieveTournamentsRepository)

  @Bean
  fun fantaPointPersistenceService(jdbcPersistPlayersPointsRepository: PersistPlayersPointsRepository): FantaPointPersistenceService =
      FantaPointPersistenceService(jdbcPersistPlayersPointsRepository)

  @Bean
  fun fantaPointService(fantaPointCalculatorService: FantaPointCalculatorService,
                        fantaPointPersistenceService: FantaPointPersistenceService,
                        retrievePlayerService: RetrievePlayerService,
                        persistPlayerService: PersistPlayerService,
                        rankingService: RankingService): FantaPointService =
      FantaPointService(fantaPointCalculatorService,
                        fantaPointPersistenceService,
                        retrievePlayerService,
                        persistPlayerService,
                        rankingService)
}