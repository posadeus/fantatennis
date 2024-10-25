package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.service.AddPlayersTeamService
import com.posadeus.fantatennis.domain.service.team.CreateTeamService
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

  @Bean
  open fun createTeamService(mySqlFantaTeamsRepository: FantaTeamsRepository,
                             mySqlFantaTournamentsRepository: FantaTournamentsRepository): CreateTeamService =
      CreateTeamService(mySqlFantaTeamsRepository,
                        mySqlFantaTournamentsRepository)

  @Bean
  open fun addPlayersTeamService(mySqlTeamsRepository: TeamsRepository): AddPlayersTeamService =
      AddPlayersTeamService(mySqlTeamsRepository)
}