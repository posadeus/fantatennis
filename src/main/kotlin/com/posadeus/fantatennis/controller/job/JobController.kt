package com.posadeus.fantatennis.controller.job

import com.posadeus.fantatennis.controller.JobApi
import com.posadeus.fantatennis.domain.exception.InvalidYearException
import com.posadeus.fantatennis.domain.model.FailureReason.PERSISTENCE_ERROR
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.*
import com.posadeus.fantatennis.domain.service.player.FantaPointService
import com.posadeus.fantatennis.domain.service.tournament.AddTournamentsService
import org.openapitools.model.JobSucceedWithErrorsDto
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

  override fun updatePlayersFantaPoints(tournamentId: Int, year: Int): ResponseEntity<JobSucceedWithErrorsDto> =
      when (val result = fantaPointService.updateFantaPointsFor(tournamentId, year)) {

        is FantaPointPersistenceSuccess -> ResponseEntity.noContent().build()
        is FantaPointPersistenceSucceedWithErrors ->
          ResponseEntity.ok(JobSucceedWithErrorsDto("Job succeed with following error: ${result.message}"))
        is FantaPointPersistenceFailure ->
          if (result.reason == PERSISTENCE_ERROR) ResponseEntity.internalServerError().build()
          else ResponseEntity.badRequest().build()
      }
}
