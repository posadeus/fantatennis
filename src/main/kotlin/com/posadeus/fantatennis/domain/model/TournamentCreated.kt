package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentCreatedDto

sealed interface TournamentCreated

data class SuccessTournamentCreated(val tournament: FantaTournamentCreatedDto) : TournamentCreated
data object ErrorTournamentCreation : TournamentCreated