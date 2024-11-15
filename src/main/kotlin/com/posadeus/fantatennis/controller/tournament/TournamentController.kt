package com.posadeus.fantatennis.controller.tournament

import com.posadeus.fantatennis.controller.TournamentApi
import com.posadeus.fantatennis.controller.model.tournament.*
import com.posadeus.fantatennis.domain.model.ErrorTournamentCreation
import com.posadeus.fantatennis.domain.model.SuccessTournamentCreated
import com.posadeus.fantatennis.domain.service.CreateTournamentService
import org.springframework.http.ResponseEntity

class TournamentController(private val createTournamentService: CreateTournamentService) : TournamentApi {

  override fun create(tournamentToCreateDto: TournamentToCreateDto): ResponseEntity<TournamentCreatedDto> =
      when (val response = createTournamentService.create(tournamentToCreateDto)) {

        is SuccessTournamentCreated -> ResponseEntity.ok(response.tournament)
        is ErrorTournamentCreation -> ResponseEntity.internalServerError().build()
      }

  override fun retrieve(tournamentId: Int): ResponseEntity<TournamentDto> {
    TODO("Not yet implemented")
  }
}
