package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsTeamsRepository
import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.service.team.TeamService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TeamServiceConfiguration {

  @Bean
  open fun teamService(mySqlFantaTournamentsTeamsRepository: FantaTournamentsTeamsRepository,
                       mySqlPlayerPointsRepository: PlayerPointsRepository): TeamService =
      TeamService(mySqlFantaTournamentsTeamsRepository,
                  mySqlPlayerPointsRepository)
}