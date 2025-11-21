package com.posadeus.fantatennis.domain.model

sealed interface FantaPointPersistence {

  data object FantaPointPersistenceSuccess: FantaPointPersistence
  data class FantaPointPersistenceSucceedWithErrors(val message: String): FantaPointPersistence
  data class FantaPointPersistenceFailure(val reason: FailureReason): FantaPointPersistence
}

enum class FailureReason {

  NO_POINTS_FOR_TOURNAMENT,
  PERSISTENCE_ERROR,
}