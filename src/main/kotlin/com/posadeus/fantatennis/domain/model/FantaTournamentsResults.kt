package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.tournament.TournamentsDto

sealed interface FantaTournamentsResults

data class FoundFantaTournamentsResults(val tournaments: TournamentsDto) : FantaTournamentsResults
data object NotFoundFantaTournaments : FantaTournamentsResults
data object ErrorFantaTournamentsResults : FantaTournamentsResults