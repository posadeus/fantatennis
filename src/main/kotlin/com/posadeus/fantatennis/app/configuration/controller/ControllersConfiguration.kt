package com.posadeus.fantatennis.app.configuration.controller

import com.posadeus.fantatennis.controller.*
import com.posadeus.fantatennis.controller.fantatournament.FantaTournamentController
import com.posadeus.fantatennis.controller.job.JobController
import com.posadeus.fantatennis.controller.ranking.RankingController
import com.posadeus.fantatennis.controller.team.TeamController
import com.posadeus.fantatennis.controller.tournament.TournamentController
import com.posadeus.fantatennis.domain.service.*
import com.posadeus.fantatennis.domain.service.fantatournament.*
import com.posadeus.fantatennis.domain.service.player.FantaPointService
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import com.posadeus.fantatennis.domain.service.team.CreateTeamService
import com.posadeus.fantatennis.domain.service.team.RetrieveTeamService
import com.posadeus.fantatennis.domain.service.tournament.AddTournamentsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ControllersConfiguration {

  @Bean
  fun fantaTournamentController(createFantaTournamentService: CreateFantaTournamentService,
                                retrieveFantaTournamentService: RetrieveFantaTournamentService,
                                retrieveFantaTournamentsService: RetrieveFantaTournamentsService): FantaTournamentApi =
      FantaTournamentController(createFantaTournamentService,
                                retrieveFantaTournamentService,
                                retrieveFantaTournamentsService)

  @Bean
  fun tournamentController(retrieveTournamentService: RetrieveTournamentService,
                           retrieveTournamentsService: RetrieveTournamentsService): TournamentApi =
      TournamentController(retrieveTournamentService,
                           retrieveTournamentsService)

  @Bean
  fun jobController(fantaPointService: FantaPointService,
                    addTournamentsService: AddTournamentsService): JobApi =
      JobController(fantaPointService,
                    addTournamentsService)

  @Bean
  fun rankingController(rankingService: RankingService): RankingApi =
      RankingController(rankingService)

  @Bean
  fun teamController(retrieveTeamService: RetrieveTeamService,
                     createTeamService: CreateTeamService,
                     addPlayersTeamService: AddPlayersTeamService,
                     swapPlayersTeamService: SwapPlayersTeamService): TeamApi =
      TeamController(retrieveTeamService,
                     createTeamService,
                     addPlayersTeamService,
                     swapPlayersTeamService)
}