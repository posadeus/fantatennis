package com.posadeus.fantatennis.domain.model

typealias Year = Int
typealias TournamentId = Int

data class DomainPlayer(val id: String,
                        val tournamentPoints: Map<Year, Map<TournamentId, Double>>)
