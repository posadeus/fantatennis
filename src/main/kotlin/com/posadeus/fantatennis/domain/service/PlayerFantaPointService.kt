package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.domain.service.player.FantaPointCalculatorService
import com.posadeus.fantatennis.domain.service.player.FantaPointPersistenceService

class PlayerFantaPointService(private val fantaPointCalculatorService: FantaPointCalculatorService,
                              private val fantaPointPersistenceService: FantaPointPersistenceService) {

  fun playerFantaPointsFor(tournamentId: Int, year: Int) {

    fantaPointPersistenceService.persistScores(fantaPointCalculatorService.calculateFantaPointsFor(tournamentId, year))
  }
}
