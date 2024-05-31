package com.posadeus.infrastructure.client.atptour.impl

import com.posadeus.infrastructure.client.atptour.AtpTourClient
import com.posadeus.infrastructure.client.atptour.model.AtpTourRankingResponse
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder.fromHttpUrl

class RestAtpTourClient(private val baseUrl: String,
                        private val restClient: RestOperations) : AtpTourClient {

  override fun retrieveRanking(positions: Int): List<AtpTourRankingResponse> {

    val response = restClient.getForEntity(composeUrl(baseUrl, positions),
                                           Array::class.java)

    return convert(response.body!!)
  }

  private fun convert(body: Array<*>): List<AtpTourRankingResponse> =
      body.map { it as Map<String, Any> }
          .map {
            AtpTourRankingResponse(rankNo = it["RankNo"].toString().toInt(),
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
