package com.posadeus.fantatennis.domain.model

import com.posadeus.fantatennis.controller.model.tournament.TournamentDto

sealed interface TournamentResults {

  data class FoundTournamentResults(val tournament: TournamentDto) : TournamentResults
  data object NotFoundTournamentId : TournamentResults
  data object ErrorTournamentResults : TournamentResults
}