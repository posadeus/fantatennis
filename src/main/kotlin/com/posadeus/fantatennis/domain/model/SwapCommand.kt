package com.posadeus.fantatennis.domain.model

typealias PlayerId = String
typealias TeamId = Int

data class SwapCommand(val teamId: TeamId,
                       val playersToRemove: Set<PlayerId>,
                       val playersToAdd: Set<PlayerId>,
                       val endingTournament: TournamentId,
                       val startingTournament: TournamentId)