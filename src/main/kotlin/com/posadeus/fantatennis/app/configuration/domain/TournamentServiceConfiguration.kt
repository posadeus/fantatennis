package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.TournamentsRegistryRepository
import com.posadeus.fantatennis.domain.infrastructure.TournamentsRepository
import com.posadeus.fantatennis.domain.service.tournament.AddTournamentsService
import com.posadeus.fantatennis.domain.service.tournament.RetrieveTournamentsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TournamentServiceConfiguration {

  @Bean
  fun addTournamentsService(tournamentsRegistryRepository: TournamentsRegistryRepository,
                            tournamentsRepository: TournamentsRepository): AddTournamentsService =
      AddTournamentsService(tournamentsRegistryRepository,
                            tournamentsRepository)

  @Bean
  fun retrieveTournamentsService(): RetrieveTournamentsService =
      RetrieveTournamentsService()
}