package com.posadeus.fantatennis.domain.model

typealias PlayerId = String

data class SwapCommand(val playersToRemove: Set<PlayerId>,
                       val playersToAdd: Set<PlayerId>,
                       val endingTournament: TournamentId,
                       val startingTournament: TournamentId)