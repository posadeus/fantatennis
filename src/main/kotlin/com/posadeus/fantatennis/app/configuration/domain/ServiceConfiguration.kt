package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.service.AddPlayersTeamService
import com.posadeus.fantatennis.domain.service.SwapPlayersTeamService
import com.posadeus.fantatennis.domain.service.fantatournament.*
import com.posadeus.fantatennis.domain.service.player.*
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import com.posadeus.fantatennis.domain.service.team.CreateTeamService
import com.posadeus.fantatennis.domain.service.team.RetrieveTeamService
import com.posadeus.fantatennis.domain.service.tournament.AddTournamentsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfiguration {

  @Bean
  fun fantaPointCalculatorService(tournamentInfoRepositories: List<TournamentInfoRepository>,
                                  jdbcRetrieveTournamentsRepository: RetrieveTournamentsRepository): FantaPointCalculatorService =
      FantaPointCalculatorService(tournamentInfoRepositories,
                                  jdbcRetrieveTournamentsRepository)

  @Bean
  fun fantaPointPersistenceService(jdbcPersistPlayersPointsRepository: PersistPlayersPointsRepository,
                                   retrievePlayerService: RetrievePlayerService,
                                   rankingService: RankingService,
                                   persistPlayerService: PersistPlayerService): FantaPointPersistenceService =
      FantaPointPersistenceService(jdbcPersistPlayersPointsRepository,
                                   retrievePlayerService,
                                   rankingService,
                                   persistPlayerService)

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

  @Bean
  fun createFantaTournamentService(jdbcCreateFantaTournamentRepository: CreateFantaTournamentRepository): CreateFantaTournamentService =
      CreateFantaTournamentService(jdbcCreateFantaTournamentRepository)

  @Bean
  fun retrieveFantaTournamentService(jdbcRetrieveFantaTournamentResultsRepository: RetrieveFantaTournamentResultsRepository): RetrieveFantaTournamentService =
      RetrieveFantaTournamentService(jdbcRetrieveFantaTournamentResultsRepository)

  @Bean
  fun retrieveFantaTournamentsService(jdbcRetrieveAllFantaTournamentsRepository: RetrieveAllFantaTournamentsRepository): RetrieveFantaTournamentsService =
      RetrieveFantaTournamentsService(jdbcRetrieveAllFantaTournamentsRepository)

  @Bean
  fun retrievePlayerService(jdbcRetrievePlayersRepository: RetrievePlayersRepository): RetrievePlayerService =
      RetrievePlayerService(jdbcRetrievePlayersRepository)

  @Bean
  fun persistPlayerService(jdbcPersistPlayersRepository: PersistPlayersRepository): PersistPlayerService =
      PersistPlayerService(jdbcPersistPlayersRepository)

  @Bean
  fun rankingService(rankingRepository: RankingRepository): RankingService =
      RankingService(rankingRepository)

  @Bean
  fun retrieveTeamService(jdbcRetrieveFantaTeamRepository: RetrieveFantaTeamRepository): RetrieveTeamService =
      RetrieveTeamService(jdbcRetrieveFantaTeamRepository)

  @Bean
  fun createTeamService(jdbcCreateTeamRepository: CreateTeamRepository): CreateTeamService =
      CreateTeamService(jdbcCreateTeamRepository)

  @Bean
  fun addPlayersTeamService(jdbcAddPlayersToTeamRepository: AddPlayersToTeamRepository): AddPlayersTeamService =
      AddPlayersTeamService(jdbcAddPlayersToTeamRepository)

  @Bean
  fun swapPlayersTeamService(jdbcSwapPlayersRepository: SwapPlayersRepository): SwapPlayersTeamService =
      SwapPlayersTeamService(jdbcSwapPlayersRepository)

  @Bean
  fun addTournamentsService(tournamentsRegistryRepository: TournamentsRegistryRepository,
                            jdbcPersistTournamentsRepository: PersistTournamentsRepository): AddTournamentsService =
      AddTournamentsService(tournamentsRegistryRepository,
                            jdbcPersistTournamentsRepository)
}