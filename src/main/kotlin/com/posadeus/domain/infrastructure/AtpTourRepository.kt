package com.posadeus.domain.infrastructure

import com.posadeus.domain.model.Ranking

interface AtpTourRepository {

  fun retrieveRanking(positions: Int): Ranking
}
