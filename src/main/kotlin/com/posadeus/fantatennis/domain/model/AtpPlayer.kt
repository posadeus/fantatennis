package com.posadeus.fantatennis.domain.model

typealias AtpPlayerId = String
typealias Year = Int
typealias TournamentId = Int

data class AtpPlayer(val id: AtpPlayerId,
                     val tournamentPoints: Map<Year, Map<TournamentId, Double>>)
