package com.posadeus.infrastructure.client.atptour

import com.posadeus.infrastructure.client.atptour.model.AtpTourRankingResponse

interface AtpTourClient {

  fun retrieveRanking(positions: Int): AtpTourRankingResponse
}
