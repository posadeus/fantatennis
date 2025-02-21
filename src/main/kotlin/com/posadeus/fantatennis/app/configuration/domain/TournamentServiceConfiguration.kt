package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.service.tournament.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TournamentServiceConfiguration {

  @Bean
  fun addTournamentsService(): AddTournamentsService =
      AddTournamentsService()

  @Bean
  fun createTournamentService(fantaTournamentsRepository: FantaTournamentsRepository): CreateTournamentService =
      CreateTournamentService(fantaTournamentsRepository)

  @Bean
  fun retrieveTournamentService(fantaTournamentsRepository: FantaTournamentsRepository): RetrieveTournamentService =
      RetrieveTournamentService(fantaTournamentsRepository)

  @Bean
  fun retrieveTournamentsService(fantaTournamentsRepository: FantaTournamentsRepository): RetrieveTournamentsService =
      RetrieveTournamentsService(fantaTournamentsRepository)
}