package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.team.TeamCreatedDto

sealed interface TeamCreation

data class TeamCreated(val team: TeamCreatedDto) : TeamCreation
data object ErrorTeamCreation : TeamCreation