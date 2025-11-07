package com.posadeus.fantatennis.domain.model

sealed interface PlayerPersistence {

  data object PlayerPersistenceSucceeded: PlayerPersistence
  data class PlayerPersistenceFailure(val message: String, val error: String?): PlayerPersistence
}