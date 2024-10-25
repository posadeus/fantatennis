package com.posadeus.fantatennis.domain.model

sealed interface FantaTournament {

  data class ValidFantaTournament(val id: Int,
                                  val startingTournamentId: Int,
                                  val endingTournamentId: Int,
                                  val tournamentYear: Int) : FantaTournament
}
