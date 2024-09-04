package com.posadeus.fantatennis.domain.model

typealias PlayerId = String
typealias Round = String

sealed interface TournamentInfo

data class CompleteTournamentInfo(val tournamentId: Int,
                                  val participants: Set<PlayerId>,
                                  val winners: Map<Round, Set<PlayerId>>) : TournamentInfo
data object ErrorTournamentInfo : TournamentInfo