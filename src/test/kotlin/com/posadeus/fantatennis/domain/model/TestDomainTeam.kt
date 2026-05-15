package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.domain.model.DomainTeam.FoundDomainTeam

object TestDomainTeam {

  fun aDomainTeam(teamId: TeamId = 0,
                  ownerId: String = "AN_OWNER_ID",
                  fantaTournamentId: Int = 1,
                  players: Map<PlayerId, Set<TournamentRange>> = emptyMap()): FoundDomainTeam =
      FoundDomainTeam(teamId = teamId,
                      ownerId = ownerId,
                      fantaTournamentId = fantaTournamentId,
                      players = players)
}