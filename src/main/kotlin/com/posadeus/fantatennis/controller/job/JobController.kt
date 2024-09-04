package com.posadeus.fantatennis.controller.job

import com.posadeus.fantatennis.controller.JobApi
import com.posadeus.fantatennis.domain.service.player.PlayerFantaPointCalculatorService
import com.posadeus.fantatennis.domain.service.player.PlayerFantaPointPersistenceService
import org.springframework.http.ResponseEntity

class JobController(private val calculatorService: PlayerFantaPointCalculatorService,
                    private val persistenceService: PlayerFantaPointPersistenceService) : JobApi {

  override fun updatePlayersFantaPoints(tournamentId: Int, year: Int): ResponseEntity<Unit> {

    persistenceService.persistScores(calculatorService.calculateFantaPointsFor(tournamentId, year))

    return ResponseEntity.noContent().build()
  }
}
