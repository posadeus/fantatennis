package com.posadeus.fantatennis.controller.job

import com.posadeus.fantatennis.controller.JobApi
import com.posadeus.fantatennis.domain.service.player.FantaPointService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class JobController(private val fantaPointService: FantaPointService) : JobApi {

  override fun updatePlayersFantaPoints(tournamentId: Int, year: Int): ResponseEntity<Unit> {

    fantaPointService.playerFantaPointsFor(tournamentId, year)

    return ResponseEntity.noContent().build()
  }
}
