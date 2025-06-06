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
  fun retrieveTeamService(jdbcRetrieveFantaTeamRepository: RetrieveFantaTeamRepository): RetrieveTeamService =
      RetrieveTeamService(jdbcRetrieveFantaTeamRepository)

  @Bean
  fun createTeamService(jdbcCreateTeamRepository: CreateTeamRepository): CreateTeamService =
      CreateTeamService(jdbcCreateTeamRepository)

  @Bean
  fun addPlayersTeamService(jdbcAddPlayersToTeamRepository: AddPlayersToTeamRepository): AddPlayersTeamService =
      AddPlayersTeamService(jdbcAddPlayersToTeamRepository)

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