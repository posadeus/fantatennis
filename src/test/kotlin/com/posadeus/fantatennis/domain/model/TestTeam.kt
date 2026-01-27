package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.team.TeamDto

object TestTeam {

  fun aTeam(owner: String = "AN_OWNER",
            players: List<PlayerPointsDto> = emptyList(),
            totalScore: Double = 0.0): FoundTeam =
      FoundTeam(team = TeamDto(owner = owner,
                               players = players,
                               totalScore = totalScore))
}