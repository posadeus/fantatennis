package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.Tournament

interface TournamentsRepository {

  fun readTournaments(year: Int): List<Tournament>
}
