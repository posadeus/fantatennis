package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.service.AddPlayersTeamService
import com.posadeus.fantatennis.domain.service.SwapPlayersTeamService
import com.posadeus.fantatennis.domain.service.player.PlayerService
import com.posadeus.fantatennis.domain.service.team.CreateTeamService
import com.posadeus.fantatennis.domain.service.team.RetrieveTeamService
import com.posadeus.fantatennis.domain.service.tournament.RetrieveTournamentsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TeamServiceConfiguration {

  @Bean
  fun retrieveTeamService(mySqlFantaTournamentsTeamsRepository: FantaTournamentsTeamsRepository,
                          mySqlPlayerPointsRepository: PlayerPointsRepository): RetrieveTeamService =
      RetrieveTeamService(mySqlFantaTournamentsTeamsRepository,
                          mySqlPlayerPointsRepository)

  @Bean
  fun createTeamService(mySqlFantaTeamsRepository: FantaTeamsRepository,
                        mySqlFantaTournamentsRepository: FantaTournamentsRepository): CreateTeamService =
      CreateTeamService(mySqlFantaTeamsRepository,
                        mySqlFantaTournamentsRepository)

  @Bean
  fun addPlayersTeamService(jdbcAddPlayerToTeamRepository: AddPlayersToTeamRepository): AddPlayersTeamService =
      AddPlayersTeamService(jdbcAddPlayerToTeamRepository)

  @Bean
  fun swapPlayersTeamService(retrieveTeamService: RetrieveTeamService,
                             playerService: PlayerService,
                             retrieveTournamentsService: RetrieveTournamentsService,
                             mySqlTeamsRepository: TeamsRepository): SwapPlayersTeamService =
      SwapPlayersTeamService(retrieveTeamService,
                             playerService,
                             retrieveTournamentsService,
                             mySqlTeamsRepository)
}