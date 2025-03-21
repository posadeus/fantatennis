package com.posadeus.fantatennis.app.configuration.controller

import com.posadeus.fantatennis.controller.TeamApi
import com.posadeus.fantatennis.controller.team.TeamController
import com.posadeus.fantatennis.domain.service.AddPlayersTeamService
import com.posadeus.fantatennis.domain.service.SwapPlayersTeamService
import com.posadeus.fantatennis.domain.service.team.CreateTeamService
import com.posadeus.fantatennis.domain.service.team.RetrieveTeamService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TeamControllerConfiguration {

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