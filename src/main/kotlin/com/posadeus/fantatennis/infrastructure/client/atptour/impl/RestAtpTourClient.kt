package com.posadeus.fantatennis.infrastructure.client.atptour.impl

import com.posadeus.fantatennis.infrastructure.client.atptour.AtpTourClient
import com.posadeus.fantatennis.infrastructure.client.atptour.model.*
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder.fromHttpUrl

class RestAtpTourClient(private val baseUrl: String,
                        private val restTemplate: RestTemplate) : AtpTourClient {

  override fun retrieveRanking(positions: Int): AtpTourRankingResponse {

    try {

      val headers = HttpHeaders()
      headers.accept = listOf(MediaType.APPLICATION_JSON, MediaType(MediaType.TEXT_PLAIN), MediaType(MediaType.ALL))
      headers.add(HttpHeaders.USER_AGENT, "*")

      val requestEntity: HttpEntity<String> = HttpEntity(headers)

      val response = restTemplate.exchange(composeUrl(baseUrl, positions),
                                           HttpMethod.GET,
                                           requestEntity,
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
