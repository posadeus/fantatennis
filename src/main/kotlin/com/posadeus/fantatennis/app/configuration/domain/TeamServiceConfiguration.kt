package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.TeamRepository
import com.posadeus.fantatennis.domain.service.team.TeamService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TeamServiceConfiguration {

  @Bean
  open fun teamService(firebaseTeamRepository: TeamRepository): TeamService =
      TeamService(firebaseTeamRepository)
}