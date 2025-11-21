package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.domain.model.FailureReason.NO_POINTS_FOR_TOURNAMENT
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceFailure

class FantaPointService(private val fantaPointCalculatorService: FantaPointCalculatorService,
                        private val fantaPointPersistenceService: FantaPointPersistenceService) {

  fun updateFantaPointsFor(tournamentId: Int, year: Int): FantaPointPersistence =
      fantaPointCalculatorService.calculateFantaPointsFor(tournamentId, year)
          .takeIf(Set<AtpPlayer>::isNotEmpty)
          ?.let(fantaPointPersistenceService::persist)
      ?: FantaPointPersistenceFailure(NO_POINTS_FOR_TOURNAMENT)
}
