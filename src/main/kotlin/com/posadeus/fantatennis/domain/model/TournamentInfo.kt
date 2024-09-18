package com.posadeus.fantatennis.domain.model

sealed interface TournamentInfo

data class CompleteTournamentInfo(val tournamentId: Int,
                                  val participants: Set<AtpPlayerId>,
                                  val winners: Map<Round, Set<AtpPlayerId>>) : TournamentInfo
data object ErrorTournamentInfo : TournamentInfo