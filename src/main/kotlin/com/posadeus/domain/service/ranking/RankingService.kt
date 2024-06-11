package com.posadeus.domain.service.ranking

import com.posadeus.domain.infrastructure.AtpTourRepository
import com.posadeus.domain.model.Ranking

class RankingService(private val atpTourRepository: AtpTourRepository) {

  fun retrieveRankedPlayer(): Ranking =
      atpTourRepository.retrieveRanking(200)
}
