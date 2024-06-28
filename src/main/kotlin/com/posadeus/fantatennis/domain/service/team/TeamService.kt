package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.domain.infrastructure.TeamRepository
import com.posadeus.fantatennis.domain.model.Team

class TeamService(private val repository: TeamRepository) {

  fun getTeam(userId: Long, teamId: String): Team =
      repository.getTeam(userId, teamId)
}
