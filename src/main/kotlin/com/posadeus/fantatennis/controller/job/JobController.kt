package com.posadeus.fantatennis.controller.job

import com.posadeus.fantatennis.controller.JobApi
import com.posadeus.fantatennis.domain.exception.NoPointsForTournamentException
import com.posadeus.fantatennis.domain.service.player.FantaPointService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class JobController(private val fantaPointService: FantaPointService) : JobApi {

  override fun updatePlayersFantaPoints(tournamentId: Int, year: Int): ResponseEntity<Unit> =
      try {

        fantaPointService.playerFantaPointsFor(tournamentId, year)

        ResponseEntity.noContent().build()
      }
      catch (e: NoPointsForTournamentException) {

        ResponseEntity.badRequest().build()
      }
      catch (e: Exception) {

        ResponseEntity.internalServerError().build()
      }
}
