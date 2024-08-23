package com.posadeus.fantatennis.infrastructure.repository.atptour

import com.posadeus.fantatennis.controller.model.ranking.RankedPlayer
import com.posadeus.fantatennis.domain.infrastructure.RankingRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.client.atptour.AtpTourClient
import com.posadeus.fantatennis.infrastructure.client.atptour.model.*

class AtpTourRankingRepository(private val atpTourClient: AtpTourClient) : RankingRepository {

  override fun retrieveRanking(positions: Int): Ranking =
      convert(atpTourClient.retrieveRanking(positions))

  private fun convert(atpTourRankingResponse: AtpTourRankingResponse): Ranking =
      when (atpTourRankingResponse) {

        is AtpTourRankingsOkResponse ->
          atpTourRankingResponse.ranking
              .map {
                RankedPlayer(id = it.playerId,
                             fullName = it.name,
                             rank = it.rankNo,
                             points = it.points.replace(",", "").toInt())
              }
              .let { RankedPlayers(it) }

        is AtpTourRankingErrorResponse ->
          EmptyRanking
      }
}
