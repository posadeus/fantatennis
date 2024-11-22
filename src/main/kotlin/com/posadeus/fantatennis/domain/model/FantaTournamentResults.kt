package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.tournament.TournamentDto

sealed interface FantaTournamentResults

data class FoundFantaTournamentResults(val tournament: TournamentDto) : FantaTournamentResults
data object NotFoundFantaTournamentId : FantaTournamentResults
data object ErrorFantaTournamentResults : FantaTournamentResults