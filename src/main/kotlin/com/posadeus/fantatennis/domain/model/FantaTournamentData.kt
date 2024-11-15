package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.tournament.TournamentDto

sealed interface FantaTournamentData

data class FoundFantaTournamentData(val tournament: TournamentDto) : FantaTournamentData
data object NotFoundFantaTournamentId : FantaTournamentData
data object ErrorFantaTournamentData : FantaTournamentData