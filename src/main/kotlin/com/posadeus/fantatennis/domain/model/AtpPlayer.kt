package com.posadeus.fantatennis.domain.model

typealias Year = Int
typealias TournamentId = Int

data class AtpPlayer(val id: String,
                     val tournamentPoints: Map<Year, Map<TournamentId, Double>>)
