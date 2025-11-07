package com.posadeus.fantatennis.domain.model

sealed interface PlayerPersistence {

  data object PlayerPersistenceSuccess: PlayerPersistence
  data class PlayerPersistenceFailure(val message: String, val error: String?): PlayerPersistence
}