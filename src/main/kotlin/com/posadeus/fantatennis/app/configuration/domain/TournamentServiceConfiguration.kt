package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.service.tournament.CreateTournamentService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TournamentServiceConfiguration {

  @Bean
  fun createTournamentService(fantaTournamentsRepository: FantaTournamentsRepository): CreateTournamentService =
      CreateTournamentService(fantaTournamentsRepository)
}