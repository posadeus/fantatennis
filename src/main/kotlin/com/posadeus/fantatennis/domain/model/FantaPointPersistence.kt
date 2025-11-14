package com.posadeus.fantatennis.domain.model

sealed interface FantaPointPersistence {

  data object FantaPointPersistenceSuccess: FantaPointPersistence
  data class FantaPointPersistenceFailure(val reason: FailureReason): FantaPointPersistence
}

enum class FailureReason {

  MISSING_PLAYERS,
  PERSISTENCE_ERROR,
  EMPTY_RANKING
}