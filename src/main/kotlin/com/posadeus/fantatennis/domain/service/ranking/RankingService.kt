package com.posadeus.fantatennis.domain.service.ranking

import com.posadeus.fantatennis.domain.infrastructure.RankingRepository
import com.posadeus.fantatennis.domain.model.Ranking

class RankingService(private val rankingRepository: RankingRepository) {

  fun retrieveRankedPlayer(positions: Int): Ranking =
      rankingRepository.retrieveRanking(positions)
}
