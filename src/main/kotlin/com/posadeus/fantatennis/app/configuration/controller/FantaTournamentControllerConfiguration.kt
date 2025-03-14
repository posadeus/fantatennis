package com.posadeus.fantatennis.app.configuration.controller

import com.posadeus.fantatennis.controller.TournamentApi
import com.posadeus.fantatennis.controller.tournament.TournamentController
import com.posadeus.fantatennis.domain.service.fantatournament.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FantaTournamentControllerConfiguration {

  @Bean
  fun tournamentController(createFantaTournamentService: CreateFantaTournamentService,
                           retrieveFantaTournamentService: RetrieveFantaTournamentService,
                           retrieveFantaTournamentsService: RetrieveFantaTournamentsService): TournamentApi =
      TournamentController(createFantaTournamentService,
                           retrieveFantaTournamentService,
                           retrieveFantaTournamentsService)
}