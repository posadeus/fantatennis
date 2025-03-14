package com.posadeus.fantatennis.controller.tournament

import com.posadeus.fantatennis.controller.TournamentApi
import com.posadeus.fantatennis.controller.model.tournament.*
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.service.fantatournament.*
import org.springframework.http.ResponseEntity

// FIXME: Rename it into FantaTournamentController and rename the interface too
class TournamentController(private val createFantaTournamentService: CreateFantaTournamentService,
                           private val retrieveFantaTournamentService: RetrieveFantaTournamentService,
                           private val retrieveFantaTournamentsService: RetrieveFantaTournamentsService) : TournamentApi {

  override fun create(tournamentToCreateDto: TournamentToCreateDto): ResponseEntity<TournamentCreatedDto> =
      when (val response = createFantaTournamentService.create(tournamentToCreateDto)) {

        is SuccessTournamentCreated -> ResponseEntity.ok(response.tournament)
        is ErrorTournamentCreation -> ResponseEntity.internalServerError().build()
      }

  override fun retrieve(tournamentId: Int): ResponseEntity<TournamentDto> =
      when (val response = retrieveFantaTournamentService.retrieve(tournamentId)) {

        is FoundFantaTournamentResults -> ResponseEntity.ok(response.tournament)
        is NotFoundFantaTournamentId -> ResponseEntity.badRequest().build()
        is ErrorFantaTournamentResults -> ResponseEntity.internalServerError().build()
      }

  override fun retrieveAll(): ResponseEntity<TournamentsDto> =
      when (val response = retrieveFantaTournamentsService.retrieveAll()) {

        is FoundFantaTournamentsResults -> ResponseEntity.ok(response.tournaments)
        is NotFoundFantaTournaments -> ResponseEntity.notFound().build()
        is ErrorFantaTournamentsResults -> ResponseEntity.internalServerError().build()
      }
}
