package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.controller.model.team.TournamentCreationDto
import com.posadeus.fantatennis.domain.model.FantaTeam
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament

interface FantaTeamsRepository {

  fun createTeam(ownerId: String, validFantaTournament: ValidFantaTournament): FantaTeam

  @Deprecated("No more possible to create a team and a tournament at the same time")
  fun createTeamAndTournament(ownerId: String, tournamentCreationDto: TournamentCreationDto): FantaTeam
}
