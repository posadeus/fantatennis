package com.posadeus.fantatennis.controller.model

import com.posadeus.fantatennis.controller.model.team.PlayerPointsDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto

object TestTournamentDto {

  fun aTournamentDto(tournamentName: String = "A_TOURNAMENT_NAME",
                     tournamentPoints: Int = 250,
                     playersScore: List<PlayerPointsDto> = emptyList()) =
      TournamentDto(tournamentName = tournamentName,
                    tournamentPoints = tournamentPoints,
                    playersScore = playersScore)
}