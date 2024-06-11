package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.Ranking

interface AtpTourRepository {

  fun retrieveRanking(positions: Int): Ranking
}
