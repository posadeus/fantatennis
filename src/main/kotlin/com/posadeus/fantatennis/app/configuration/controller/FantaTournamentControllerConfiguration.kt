package com.posadeus.fantatennis.app.configuration.controller

import com.posadeus.fantatennis.controller.FantaTournamentApi
import com.posadeus.fantatennis.controller.fantatournament.FantaTournamentController
import com.posadeus.fantatennis.domain.service.fantatournament.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FantaTournamentControllerConfiguration {

  @Bean
  fun tournamentController(createFantaTournamentService: CreateFantaTournamentService,
                           retrieveFantaTournamentService: RetrieveFantaTournamentService,
                           retrieveFantaTournamentsService: RetrieveFantaTournamentsService): FantaTournamentApi =
      FantaTournamentController(createFantaTournamentService,
                                retrieveFantaTournamentService,
                                retrieveFantaTournamentsService)
}