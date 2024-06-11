package com.posadeus.infrastructure.repository.atptour

import com.posadeus.controller.model.ranking.RankedPlayer
import com.posadeus.domain.infrastructure.AtpTourRepository
import com.posadeus.domain.model.EmptyRanking
import com.posadeus.domain.model.RankedPlayers
import com.posadeus.domain.model.Ranking
import com.posadeus.infrastructure.client.atptour.AtpTourClient
import com.posadeus.infrastructure.client.atptour.model.AtpTourRankingErrorResponse
import com.posadeus.infrastructure.client.atptour.model.AtpTourRankingResponse
import com.posadeus.infrastructure.client.atptour.model.AtpTourRankingsOkResponse

class AtpTourRepositoryImpl(private val atpTourClient: AtpTourClient) : AtpTourRepository {

  override fun retrieveRanking(positions: Int): Ranking =
      convert(atpTourClient.retrieveRanking(positions))

  private fun convert(atpTourRankingResponse: AtpTourRankingResponse): Ranking =
      when (atpTourRankingResponse) {

        is AtpTourRankingsOkResponse ->
          atpTourRankingResponse.ranking
              .map {
                RankedPlayer(id = it.playerId,
                             name = it.name,
                             surname = it.name,
                             age = 0,
                             rank = it.rankNo,
                             points = it.points.replace(",", "").toInt())
              }
              .let { RankedPlayers(it) }

        is AtpTourRankingErrorResponse ->
          EmptyRanking
      }
}
