package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentsDto

sealed interface FantaTournamentsResults

data class FoundFantaTournamentsResults(val tournaments: FantaTournamentsDto) : FantaTournamentsResults
data object NotFoundFantaTournaments : FantaTournamentsResults
data object ErrorFantaTournamentsResults : FantaTournamentsResults