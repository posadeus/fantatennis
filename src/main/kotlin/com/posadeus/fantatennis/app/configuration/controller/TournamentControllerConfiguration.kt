package com.posadeus.fantatennis.app.configuration.controller

import com.posadeus.fantatennis.controller.TournamentApi
import com.posadeus.fantatennis.controller.tournament.TournamentController
import com.posadeus.fantatennis.domain.service.CreateTournamentService
import com.posadeus.fantatennis.domain.service.RetrieveTournamentService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TournamentControllerConfiguration {

  @Bean
  fun tournamentController(createTournamentService: CreateTournamentService,
                           retrieveTournamentService: RetrieveTournamentService): TournamentApi =
      TournamentController(createTournamentService,
                           retrieveTournamentService)
}