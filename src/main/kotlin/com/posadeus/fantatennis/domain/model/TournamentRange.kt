package com.posadeus.fantatennis.domain.model

data class TournamentRange(val start: Int,
                           val end: Int? = null)

fun TournamentRange.contains(tournamentId: TournamentId): Boolean =
    tournamentId >= start && (end == null || tournamentId <= end)