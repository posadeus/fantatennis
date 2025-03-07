package com.posadeus.fantatennis.controller.job

import com.posadeus.fantatennis.controller.JobApi
import com.posadeus.fantatennis.domain.exception.InvalidYearException
import com.posadeus.fantatennis.domain.exception.NoPointsForTournamentException
import com.posadeus.fantatennis.domain.service.player.FantaPointService
import com.posadeus.fantatennis.domain.service.tournament.AddTournamentsService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class JobController(private val fantaPointService: FantaPointService,
                    private val addTournamentsService: AddTournamentsService) : JobApi {

  override fun addTournamentsForYear(year: Int): ResponseEntity<Unit> =
      try {

        addTournamentsService.addTournamentsFor(year)

        ResponseEntity.noContent().build()
      }
      catch (e: InvalidYearException) {

        ResponseEntity.badRequest().build()
      }
      catch (e: Exception) {

        ResponseEntity.internalServerError().build()
      }

  override fun updatePlayersFantaPoints(tournamentId: Int, year: Int): ResponseEntity<Unit> =
      try {

        fantaPointService.playerFantaPointsFor(tournamentId, year)

        ResponseEntity.noContent().build()
      }
      catch (e: NoPointsForTournamentException) {

        ResponseEntity.badRequest().build()
      }
      catch (e: Exception) {

        LOGGER.error(e.message, e.printStackTrace())
        ResponseEntity.internalServerError().build()
      }

  companion object {

    private val LOGGER = LoggerFactory.getLogger(JobController::class.java)
  }
}
