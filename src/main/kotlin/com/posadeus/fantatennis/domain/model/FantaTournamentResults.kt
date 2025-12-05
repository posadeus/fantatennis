package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentDto

sealed interface FantaTournamentResults

data class FoundFantaTournamentResults(val tournament: FantaTournamentDto) : FantaTournamentResults
data object NotFoundFantaTournamentId : FantaTournamentResults
data object ErrorFantaTournamentResults : FantaTournamentResults