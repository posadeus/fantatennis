package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.*
import com.posadeus.fantatennis.domain.service.fantatournament.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FantaTournamentServiceConfiguration {

  @Bean
  fun createFantaTournamentService(jdbcCreateFantaTournamentRepository: CreateFantaTournamentRepository): CreateFantaTournamentService =
      CreateFantaTournamentService(jdbcCreateFantaTournamentRepository)

  @Bean
  fun retrieveFantaTournamentService(jdbcRetrieveFantaTournamentResultsRepository: RetrieveFantaTournamentResultsRepository): RetrieveFantaTournamentService =
      RetrieveFantaTournamentService(jdbcRetrieveFantaTournamentResultsRepository)

  @Bean
  fun retrieveFantaTournamentsService(jdbcRetrieveAllFantaTournamentsRepository: RetrieveAllFantaTournamentsRepository): RetrieveFantaTournamentsService =
      RetrieveFantaTournamentsService(jdbcRetrieveAllFantaTournamentsRepository)
}