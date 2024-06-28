package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.team.TeamDto

sealed interface Team

data class FoundTeam(val team: TeamDto) : Team
data object EmptyTeam : Team
data object TeamIdNotFoundTeam : Team
data object UserIdNotFoundTeam : Team
data object ErrorTeam : Team
