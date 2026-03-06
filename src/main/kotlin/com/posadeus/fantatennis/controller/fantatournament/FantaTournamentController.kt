package com.posadeus.fantatennis.controller.fantatournament

import com.posadeus.fantatennis.controller.FantaTournamentApi
import com.posadeus.fantatennis.controller.model.fantatournament.*
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FantaTournamentResults.*
import com.posadeus.fantatennis.domain.service.fantatournament.*
import org.springframework.http.ResponseEntity

class FantaTournamentController(private val createFantaTournamentService: CreateFantaTournamentService,
                                private val retrieveFantaTournamentService: RetrieveFantaTournamentService,
                                private val retrieveFantaTournamentsService: RetrieveFantaTournamentsService) : FantaTournamentApi {

  override fun create(fantaTournamentToCreateDto: FantaTournamentToCreateDto): ResponseEntity<FantaTournamentCreatedDto> =
      when (val response = createFantaTournamentService.create(fantaTournamentToCreateDto)) {

        is SuccessTournamentCreated -> ResponseEntity.ok(response.tournament)
        is ErrorTournamentCreation -> ResponseEntity.internalServerError().build()
      }

  override fun retrieve(tournamentId: Int): ResponseEntity<FantaTournamentDto> =
      when (val response = retrieveFantaTournamentService.retrieve(tournamentId)) {

        is FoundFantaTournamentResults -> ResponseEntity.ok(response.tournament)
        is NotFoundFantaTournamentId -> ResponseEntity.badRequest().build()
        is ErrorFantaTournamentResults -> ResponseEntity.internalServerError().build()
      }

  override fun retrieveAll(): ResponseEntity<FantaTournamentsDto> =
      when (val response = retrieveFantaTournamentsService.retrieveAll()) {

        is FoundFantaTournamentsResults -> ResponseEntity.ok(response.tournaments)
        is NotFoundFantaTournaments -> ResponseEntity.notFound().build()
        is ErrorFantaTournamentsResults -> ResponseEntity.internalServerError().build()
      }
}
