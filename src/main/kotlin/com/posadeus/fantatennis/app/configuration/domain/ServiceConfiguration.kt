package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.service.*
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
                                  sqlRetrieveTournamentsRepository: RetrieveTournamentsRepository): FantaPointCalculatorService =
      FantaPointCalculatorService(tournamentInfoRepositories,
                                  sqlRetrieveTournamentsRepository)

  @Bean
  fun fantaPointPersistenceService(sqlPersistPlayersPointsRepository: PersistPlayersPointsRepository,
                                   sqlRetrievePlayersRepository: RetrievePlayersRepository,
                                   rankingRepository: RankingRepository,
                                   sqlPersistPlayersRepository: PersistPlayersRepository): FantaPointPersistenceService =
      FantaPointPersistenceService(sqlPersistPlayersPointsRepository,
                                   sqlRetrievePlayersRepository,
                                   rankingRepository,
                                   sqlPersistPlayersRepository)

  @Bean
  fun fantaPointService(fantaPointCalculatorService: FantaPointCalculatorService,
                        fantaPointPersistenceService: FantaPointPersistenceService): FantaPointService =
      FantaPointService(fantaPointCalculatorService,
                        fantaPointPersistenceService)

  @Bean
  fun createFantaTournamentService(sqlCreateFantaTournamentRepository: CreateFantaTournamentRepository): CreateFantaTournamentService =
      CreateFantaTournamentService(sqlCreateFantaTournamentRepository)

  @Bean
  fun retrieveFantaTournamentService(sqlRetrieveFantaTournamentsRepository: RetrieveFantaTournamentsRepository,
                                     sqlRetrievePlayersPointsRepository: RetrievePlayersPointsRepository,
                                     sqlRetrieveFantaTeamRepository: RetrieveFantaTeamRepository): RetrieveFantaTournamentService =
      RetrieveFantaTournamentService(sqlRetrieveFantaTournamentsRepository,
                                     sqlRetrievePlayersPointsRepository,
                                     sqlRetrieveFantaTeamRepository)

  @Bean
  fun retrieveFantaTournamentsService(sqlRetrieveFantaTournamentsRepository: RetrieveFantaTournamentsRepository): RetrieveFantaTournamentsService =
      RetrieveFantaTournamentsService(sqlRetrieveFantaTournamentsRepository)

  @Bean
  fun retrieveTournamentService(sqlRetrieveTournamentsRepository: RetrieveTournamentsRepository,
                                sqlRetrievePlayersPointsRepository: RetrievePlayersPointsRepository): RetrieveTournamentService =
      RetrieveTournamentService(sqlRetrieveTournamentsRepository,
                                sqlRetrievePlayersPointsRepository)

  @Bean
  fun retrieveTournamentsService(): RetrieveTournamentsService =
      RetrieveTournamentsService()

  @Bean
  fun rankingService(rankingRepository: RankingRepository): RankingService =
      RankingService(rankingRepository)

  @Bean
  fun retrieveTeamService(sqlRetrieveFantaTeamRepository: RetrieveFantaTeamRepository,
                          sqlRetrieveFantaTournamentsRepository: RetrieveFantaTournamentsRepository,
                          sqlRetrievePlayersPointsRepository: RetrievePlayersPointsRepository): RetrieveTeamService =
      RetrieveTeamService(sqlRetrieveFantaTeamRepository,
                          sqlRetrieveFantaTournamentsRepository,
                          sqlRetrievePlayersPointsRepository)

  @Bean
  fun createTeamService(sqlCreateTeamRepository: CreateTeamRepository): CreateTeamService =
      CreateTeamService(sqlCreateTeamRepository)

  @Bean
  fun addPlayersTeamService(sqlPersistTeamPlayersRepository: PersistTeamPlayersRepository,
                            jdbcRetrieveFantaTeamRepository: RetrieveFantaTeamRepository,
                            sqlRetrieveTournamentsRepository: RetrieveTournamentsRepository,
                            sqlRetrievePlayersRepository: RetrievePlayersRepository): AddPlayersTeamService =
      AddPlayersTeamService(sqlPersistTeamPlayersRepository,
                            jdbcRetrieveFantaTeamRepository,
                            sqlRetrieveTournamentsRepository,
                            sqlRetrievePlayersRepository)

  @Bean
  fun swapPlayersTeamService(jdbcSwapPlayersRepository: SwapPlayersRepository): SwapPlayersTeamService =
      SwapPlayersTeamService(jdbcSwapPlayersRepository)

  @Bean
  fun addTournamentsService(tournamentsRegistryRepository: TournamentsRegistryRepository,
                            sqlPersistTournamentsRepository: PersistTournamentsRepository): AddTournamentsService =
      AddTournamentsService(tournamentsRegistryRepository,
                            sqlPersistTournamentsRepository)
}