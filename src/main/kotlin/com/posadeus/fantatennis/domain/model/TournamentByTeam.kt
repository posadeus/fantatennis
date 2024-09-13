package com.posadeus.fantatennis.domain.model

data class TournamentByTeam(val teamId: Int,
                            val startingTournamentId: Int,
                            val endingTournamentId: Int,
                            val tournamentYear: Int)
