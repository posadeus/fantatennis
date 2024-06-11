package com.posadeus.fantatennis.infrastructure.client.atptour

import com.posadeus.fantatennis.infrastructure.client.atptour.model.AtpTourRankingResponse

interface AtpTourClient {

  fun retrieveRanking(positions: Int): AtpTourRankingResponse
}
