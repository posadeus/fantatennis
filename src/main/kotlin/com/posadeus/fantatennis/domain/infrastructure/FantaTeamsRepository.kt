package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.controller.model.team.TournamentCreationDto
import com.posadeus.fantatennis.domain.model.FantaTeam

interface FantaTeamsRepository {

  fun createTeam(ownerId: String, tournamentId: Int): FantaTeam
  fun createTeamAndTournament(ownerId: String, tournamentCreationDto: TournamentCreationDto): FantaTeam
}
