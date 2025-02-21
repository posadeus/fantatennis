package com.posadeus.fantatennis.domain.model

sealed interface TournamentsRegistry {

  data class FoundTournamentsRegistry(val tournaments: List<TournamentRegistry>) : TournamentsRegistry
}