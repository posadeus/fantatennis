package com.posadeus.fantatennis.domain.service.ranking

import com.posadeus.fantatennis.domain.infrastructure.AtpTourRepository
import com.posadeus.fantatennis.domain.model.Ranking

class RankingService(private val atpTourRepository: AtpTourRepository) {

  fun retrieveRankedPlayer(): Ranking =
      atpTourRepository.retrieveRanking(200)
}
