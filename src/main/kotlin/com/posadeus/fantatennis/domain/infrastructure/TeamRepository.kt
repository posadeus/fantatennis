package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.Team

interface TeamRepository {

  fun getTeam(userId: String, teamId: String): Team
}
