package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.service.fantatournament.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FantaTournamentServiceConfiguration {

  @Bean
  fun createTournamentService(fantaTournamentsRepository: FantaTournamentsRepository): CreateFantaTournamentService =
      CreateFantaTournamentService(fantaTournamentsRepository)

  @Bean
  fun retrieveTournamentService(fantaTournamentsRepository: FantaTournamentsRepository): RetrieveFantaTournamentService =
      RetrieveFantaTournamentService(fantaTournamentsRepository)

  @Bean
  fun retrieveTournamentsService(fantaTournamentsRepository: FantaTournamentsRepository): RetrieveFantaTournamentsService =
      RetrieveFantaTournamentsService(fantaTournamentsRepository)
}