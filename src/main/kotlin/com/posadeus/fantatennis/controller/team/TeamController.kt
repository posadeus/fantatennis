package com.posadeus.fantatennis.controller.team

import com.posadeus.fantatennis.controller.TeamApi
import com.posadeus.fantatennis.controller.model.team.*
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.Swap.SwapCompleted
import com.posadeus.fantatennis.domain.model.Swap.SwapFailed
import com.posadeus.fantatennis.domain.service.AddPlayersTeamService
import com.posadeus.fantatennis.domain.service.SwapPlayersTeamService
import com.posadeus.fantatennis.domain.service.team.CreateTeamService
import com.posadeus.fantatennis.domain.service.team.RetrieveTeamService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class TeamController(private val retrieveTeamService: RetrieveTeamService,
                     private val createTeamService: CreateTeamService,
                     private val addPlayersTeamService: AddPlayersTeamService,
                     private val swapPlayersTeamService: SwapPlayersTeamService) : TeamApi {

  override fun addPlayers(teamId: Int, playersToAddDto: PlayersToAddDto): ResponseEntity<TeamDto> =
      when (val team = addPlayersTeamService.addPlayers(teamId, playersToAddDto.playerIds, playersToAddDto.startingTournamentId)) {

        is FoundTeam -> ResponseEntity.ok(team.team)
        is TeamIdNotFoundTeam -> ResponseEntity.badRequest().build()
        is ErrorTeam -> ResponseEntity.internalServerError().build()
      }

  override fun create(teamToCreateDto: TeamToCreateDto): ResponseEntity<TeamCreatedDto> =
      when (val teamCreation = createTeamService.create(teamToCreateDto)) {

        is TeamCreated -> ResponseEntity.status(201).body(teamCreation.team)
        is ErrorTeamCreation -> ResponseEntity.internalServerError().build()
      }

  override fun retrieve(teamId: Int): ResponseEntity<TeamDto> =
      when (val team = retrieveTeamService.retrieve(teamId)) {

        is FoundTeam -> ResponseEntity.ok(team.team)
        is TeamIdNotFoundTeam -> ResponseEntity.badRequest().build()
        is ErrorTeam -> ResponseEntity.internalServerError().build()
      }

  override fun swap(teamId: Int, playersToSwapDto: PlayersToSwapDto): ResponseEntity<Unit> =
      when (swapPlayersTeamService.swap(teamId, playersToSwapDto)) {

        is SwapCompleted -> ResponseEntity.noContent().build()
        is SwapFailed -> ResponseEntity.internalServerError().build()
      }
}
