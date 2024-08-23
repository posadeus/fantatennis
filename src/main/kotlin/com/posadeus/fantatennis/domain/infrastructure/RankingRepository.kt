package com.posadeus.fantatennis.domain.infrastructure

import com.posadeus.fantatennis.domain.model.Ranking

interface RankingRepository {

  fun retrieveRanking(positions: Int): Ranking
}
