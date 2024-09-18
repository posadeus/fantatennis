package com.posadeus.fantatennis.domain.model

sealed interface TournamentByTeam

data class FoundTournamentByTeam(val teamId: Int,
                                 val startingTournamentId: Int,
                                 val endingTournamentId: Int,
                                 val tournamentYear: Int) : TournamentByTeam

data object EmptyTournamentByTeam : TournamentByTeam
