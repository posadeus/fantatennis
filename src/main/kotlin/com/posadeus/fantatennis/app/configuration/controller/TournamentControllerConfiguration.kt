package com.posadeus.fantatennis.app.configuration.controller

import com.posadeus.fantatennis.controller.TournamentApi
import com.posadeus.fantatennis.controller.tournament.TournamentController
import com.posadeus.fantatennis.domain.service.tournament.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TournamentControllerConfiguration {

  @Bean
  fun tournamentController(createTournamentService: CreateTournamentService,
                           retrieveTournamentService: RetrieveTournamentService,
                           retrieveTournamentsService: RetrieveTournamentsService): TournamentApi =
      TournamentController(createTournamentService,
                           retrieveTournamentService,
                           retrieveTournamentsService)
}