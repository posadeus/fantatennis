package com.posadeus.fantatennis.controller.tournament

import com.posadeus.fantatennis.controller.TournamentApi
import com.posadeus.fantatennis.controller.model.tournament.*
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.service.tournament.*
import org.springframework.http.ResponseEntity

class TournamentController(private val createTournamentService: CreateTournamentService,
                           private val retrieveTournamentService: RetrieveTournamentService,
                           private val retrieveTournamentsService: RetrieveTournamentsService) : TournamentApi {

  override fun create(tournamentToCreateDto: TournamentToCreateDto): ResponseEntity<TournamentCreatedDto> =
      when (val response = createTournamentService.create(tournamentToCreateDto)) {

        is SuccessTournamentCreated -> ResponseEntity.ok(response.tournament)
        is ErrorTournamentCreation -> ResponseEntity.internalServerError().build()
      }

  override fun retrieve(tournamentId: Int): ResponseEntity<TournamentDto> =
      when (val response = retrieveTournamentService.retrieve(tournamentId)) {

        is FoundFantaTournamentResults -> ResponseEntity.ok(response.tournament)
        is NotFoundFantaTournamentId -> ResponseEntity.badRequest().build()
        is ErrorFantaTournamentResults -> ResponseEntity.internalServerError().build()
      }

  override fun retrieveAll(): ResponseEntity<TournamentsDto> =
      when (val response = retrieveTournamentsService.retrieveAll()) {

        is FoundFantaTournamentsResults -> ResponseEntity.ok(response.tournaments)
        is NotFoundFantaTournaments -> ResponseEntity.notFound().build()
        is ErrorFantaTournamentsResults -> ResponseEntity.internalServerError().build()
      }
}
