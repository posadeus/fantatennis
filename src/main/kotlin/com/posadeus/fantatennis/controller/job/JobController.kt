package com.posadeus.fantatennis.controller.job

import com.posadeus.fantatennis.controller.JobApi
import com.posadeus.fantatennis.domain.service.player.FantaPointCalculatorService
import com.posadeus.fantatennis.domain.service.player.FantaPointPersistenceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class JobController(private val calculatorService: FantaPointCalculatorService,
                    private val persistenceService: FantaPointPersistenceService) : JobApi {

  override fun updatePlayersFantaPoints(tournamentId: Int, year: Int): ResponseEntity<Unit> {

    persistenceService.persistScores(calculatorService.calculateFantaPointsFor(tournamentId, year))

    return ResponseEntity.noContent().build()
  }
}
