package com.posadeus.fantatennis.controller.team

import com.posadeus.fantatennis.controller.TeamApi
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.service.team.TeamService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class TeamController(private val service: TeamService) : TeamApi {

  override fun team(userId: Long, teamId: String): ResponseEntity<TeamDto> =
      when (val team = service.getTeam(userId, teamId)) {

        is FoundTeam -> ResponseEntity.ok(team.team)
        is EmptyTeam -> ResponseEntity.noContent().build()
        is UserIdNotFoundTeam, TeamIdNotFoundTeam -> ResponseEntity.badRequest().build()
        is ErrorTeam -> ResponseEntity.internalServerError().build()
      }
}
