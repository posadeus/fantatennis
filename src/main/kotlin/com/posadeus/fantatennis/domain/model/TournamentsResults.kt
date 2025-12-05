package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.tournament.TournamentsDto

sealed interface TournamentsResults

data class FoundTournamentsResults(val tournaments: TournamentsDto) : TournamentsResults
data object NotFoundTournaments : TournamentsResults
data object ErrorTournamentsResults : TournamentsResults