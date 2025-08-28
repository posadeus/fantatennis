package com.posadeus.fantatennis.infrastructure.repository.atptour

import com.posadeus.fantatennis.controller.model.ranking.RankedPlayerDto
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

  private fun toRankedPlayer(atpTourRankingOkResponse: AtpTourRankingOkResponse): RankedPlayerDto =
      RankedPlayerDto(id = atpTourRankingOkResponse.PlayerId,
                      fullName = atpTourRankingOkResponse.Name,
                      rank = atpTourRankingOkResponse.RankNo,
                      points = atpTourRankingOkResponse.Points.replace(",", "").toInt())
}
