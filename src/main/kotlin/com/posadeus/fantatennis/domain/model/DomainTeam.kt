package com.posadeus.fantatennis.domain.model

sealed interface DomainTeam {

  data class FoundDomainTeam(val teamId: TeamId,
                             val ownerId: String,
                             val fantaTournamentId: Int,
                             val players: Map<PlayerId, TournamentRange>): DomainTeam

  data class NotFoundDomainTeam(val teamId: TeamId) : DomainTeam
}
