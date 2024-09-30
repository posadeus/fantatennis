package com.posadeus.fantatennis.infrastructure.repository.atptour

import com.posadeus.fantatennis.controller.model.ranking.RankedPlayer
import com.posadeus.fantatennis.domain.infrastructure.RankingRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.client.atptour.AtpTourClient
import com.posadeus.fantatennis.infrastructure.client.atptour.model.*

class AtpTourRankingRepository(private val atpTourClient: AtpTourClient) : RankingRepository {

  override fun retrieveRanking(positions: Int): Ranking =
      atpTourClient.retrieveRanking(positions)
          .let(::convert)

  private fun convert(atpTourRankingResponse: AtpTourRankingResponse): Ranking =
      when (atpTourRankingResponse) {

        is AtpTourRankingsOkResponse ->
          atpTourRankingResponse.ranking
              .map(::toRankedPlayer)
              .let(::RankedPlayers)

        is AtpTourRankingErrorResponse -> EmptyRanking
      }

  private fun toRankedPlayer(atpTourRankingOkResponse: AtpTourRankingOkResponse): RankedPlayer =
      RankedPlayer(id = atpTourRankingOkResponse.playerId,
                   fullName = atpTourRankingOkResponse.name,
                   rank = atpTourRankingOkResponse.rankNo,
                   points = atpTourRankingOkResponse.points.replace(",", "").toInt())
}
