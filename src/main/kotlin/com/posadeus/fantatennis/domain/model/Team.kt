package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.team.TeamDto

sealed interface Team

data class FoundTeam(val team: TeamDto) : Team
data object TeamIdNotFoundTeam : Team // FIXME: Maybe it's better to have NotFound(message: String)
data object ErrorTeam : Team // FIXME: Add a message?
