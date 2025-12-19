package com.posadeus.fantatennis.controller.tournament

import com.posadeus.fantatennis.controller.TournamentApi
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentsDto
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.TournamentResults.*
import com.posadeus.fantatennis.domain.service.RetrieveTournamentService
import com.posadeus.fantatennis.domain.service.RetrieveTournamentsService
import org.springframework.http.ResponseEntity

class TournamentController(private val retrieveTournamentService: RetrieveTournamentService,
                           private val retrieveTournamentsService: RetrieveTournamentsService) : TournamentApi {

  override fun retrieve(tournamentId: Int): ResponseEntity<TournamentDto> =
      when (val response = retrieveTournamentService.retrieve(tournamentId)) {

        is FoundTournamentResults -> ResponseEntity.ok(response.tournament)
        is NotFoundTournamentId -> ResponseEntity.badRequest().build()
        is ErrorTournamentResults -> ResponseEntity.internalServerError().build()
      }

  override fun retrieveAll(year: Int): ResponseEntity<TournamentsDto> =
      when (val response = retrieveTournamentsService.retrieveAllBy(year)) {

        is FoundTournamentsResults -> ResponseEntity.ok(response.tournaments)
        is NotFoundTournaments -> ResponseEntity.notFound().build()
        is ErrorTournamentsResults -> ResponseEntity.internalServerError().build()
      }
}
