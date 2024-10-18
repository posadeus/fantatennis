package com.posadeus.fantatennis.app.configuration.controller

import com.posadeus.fantatennis.controller.TeamApi
import com.posadeus.fantatennis.controller.team.TeamController
import com.posadeus.fantatennis.domain.service.AddPlayersTeamService
import com.posadeus.fantatennis.domain.service.team.CreateTeamService
import com.posadeus.fantatennis.domain.service.team.TeamService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TeamControllerConfiguration {

  @Bean
  open fun teamController(teamService: TeamService,
                          createTeamService: CreateTeamService,
                          addPlayersTeamService: AddPlayersTeamService): TeamApi =
      TeamController(teamService,
                     createTeamService,
                     addPlayersTeamService)
}