package com.posadeus.fantatennis.domain.model

@Deprecated("Use FantaTournaments")
sealed interface FantaTournament {

  // FIXME Remove interface and rename it into FantaTournament
  data class ValidFantaTournament(val id: Int,
                                  val startingTournamentId: Int,
                                  val endingTournamentId: Int,
                                  val tournamentYear: Int) : FantaTournament

  data object InvalidFantaTournament : FantaTournament
}
