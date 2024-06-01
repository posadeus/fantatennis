package com.posadeus.infrastructure.client.atptour.impl

import com.posadeus.infrastructure.client.atptour.AtpTourClient
import com.posadeus.infrastructure.client.atptour.model.AtpTourRankingErrorResponse
import com.posadeus.infrastructure.client.atptour.model.AtpTourRankingOkResponse
import com.posadeus.infrastructure.client.atptour.model.AtpTourRankingResponse
import com.posadeus.infrastructure.client.atptour.model.AtpTourRankingsOkResponse
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder.fromHttpUrl

class RestAtpTourClient(private val baseUrl: String,
                        private val restClient: RestOperations) : AtpTourClient {

  override fun retrieveRanking(positions: Int): AtpTourRankingResponse {

    try {

      val response = restClient.getForEntity(composeUrl(baseUrl, positions),
                                             Array::class.java)

      return convert(response.body!!)
    }
    catch (e: Exception) {

      return AtpTourRankingErrorResponse
    }
  }

  private fun convert(body: Array<*>): AtpTourRankingsOkResponse =
      body.map { it as Map<String, Any> }
          .map {
            AtpTourRankingOkResponse(rankNo = it["RankNo"].toString().toInt(),
                                     name = it["Name"].toString(),
                                     points = it["Points"].toString(),
                                     urlHeadshotImage = it["UrlHeadshotImage"].toString(),
                                     urlCountryFlag = it["UrlCountryFlag"].toString(),
                                     movement = it["Movement"].toString().toInt(),
                                     country = it["Country"].toString(),
                                     countryCode = it["CountryCode"].toString(),
                                     playerId = it["PlayerId"].toString(),
                                     playerProfileUrl = it["PlayerProfileUrl"].toString())
          }
          .let { AtpTourRankingsOkResponse(it) }

  private fun composeUrl(baseUrl: String, positions: Int): String =
      fromHttpUrl(baseUrl)
          .path(RANKING_PATH)
          .queryParam("v", 1)
          .buildAndExpand(positions)
          .toString()

  companion object {

    private const val RANKING_PATH = "/en/-/www/rank/sglroll/{positions}"
  }
}
