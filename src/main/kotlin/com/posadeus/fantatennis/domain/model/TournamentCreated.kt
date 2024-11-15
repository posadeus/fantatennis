package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.tournament.TournamentCreatedDto

sealed interface TournamentCreated

data class SuccessTournamentCreated(val tournament: TournamentCreatedDto) : TournamentCreated
data object ErrorTournamentCreation : TournamentCreated