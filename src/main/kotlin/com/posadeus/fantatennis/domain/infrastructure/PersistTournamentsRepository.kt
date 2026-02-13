package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.TournamentsRegistry.FoundTournamentsRegistry

interface PersistTournamentsRepository {

  fun persistNewTournaments(tournaments: FoundTournamentsRegistry)
}
