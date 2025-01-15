package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.service.AddPlayersTeamService
import com.posadeus.fantatennis.domain.service.team.CreateTeamService
import com.posadeus.fantatennis.domain.service.team.RetrieveTeamService
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
  fun addPlayersTeamService(mySqlTeamsRepository: TeamsRepository): AddPlayersTeamService =
      AddPlayersTeamService(mySqlTeamsRepository)
}