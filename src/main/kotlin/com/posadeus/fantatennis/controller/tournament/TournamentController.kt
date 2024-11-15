package com.posadeus.fantatennis.controller.tournament

import com.posadeus.fantatennis.controller.TournamentApi
import com.posadeus.fantatennis.controller.model.tournament.*
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.service.tournament.CreateTournamentService
import com.posadeus.fantatennis.domain.service.tournament.RetrieveTournamentService
import org.springframework.http.ResponseEntity

class TournamentController(private val createTournamentService: CreateTournamentService,
                           private val retrieveTournamentService: RetrieveTournamentService) : TournamentApi {

  override fun create(tournamentToCreateDto: TournamentToCreateDto): ResponseEntity<TournamentCreatedDto> =
      when (val response = createTournamentService.create(tournamentToCreateDto)) {

        is SuccessTournamentCreated -> ResponseEntity.ok(response.tournament)
        is ErrorTournamentCreation -> ResponseEntity.internalServerError().build()
      }

  override fun retrieve(tournamentId: Int): ResponseEntity<TournamentDto> =
      when (val response = retrieveTournamentService.retrieve(tournamentId)) {

        is FoundFantaTournamentData -> ResponseEntity.ok(response.tournament)
        is NotFoundFantaTournamentId -> ResponseEntity.badRequest().build()
        is ErrorFantaTournamentData -> ResponseEntity.internalServerError().build()
      }
}
