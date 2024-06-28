package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.Team

interface TeamRepository {

  fun getTeam(userId: Long, teamId: String): Team {
    TODO("Not yet implemented")
  }
}
