package com.posadeus.fantatennis.domain.model

sealed interface TournamentsCreated {

  data object SuccessTournamentsCreated : TournamentsCreated
  data class ErrorTournamentsCreation(val error: String) : TournamentsCreated
}