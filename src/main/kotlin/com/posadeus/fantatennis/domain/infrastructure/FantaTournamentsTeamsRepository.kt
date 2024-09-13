package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.TournamentByTeam

interface FantaTournamentsTeamsRepository {

  fun retrieveTournamentByTeamId(teamId: Int): TournamentByTeam
}
