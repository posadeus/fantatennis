package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.TournamentRepository
import com.posadeus.fantatennis.domain.service.FantaPointCalculatorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class FantaPointCalculatorServiceConfiguration {

  @Bean
  open fun fantaPointCalculatorService(tennisTvTournamentRepository: TournamentRepository): FantaPointCalculatorService =
      FantaPointCalculatorService(tennisTvTournamentRepository)
}