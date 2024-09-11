package com.posadeus.fantatennis.infrastructure.client.wimbledon.impl

import com.google.gson.Gson
import com.posadeus.fantatennis.infrastructure.client.wimbledon.WimbledonClient
import com.posadeus.fantatennis.infrastructure.client.wimbledon.model.*
import org.springframework.http.*
import org.springframework.http.MediaType.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

class RestWimbledonClient(private val baseUrl: String,
                          private val restTemplate: RestTemplate) : WimbledonClient {

  override fun retrieveDraws(year: Int): WimbledonResponse {

    try {

      val headers = HttpHeaders()
      headers.accept = listOf(APPLICATION_JSON, MediaType(TEXT_PLAIN), MediaType(ALL))
      headers.add(HttpHeaders.USER_AGENT, "*")

      val requestEntity: HttpEntity<String> = HttpEntity(headers)

      val response = restTemplate.exchange(composeUrl(baseUrl, year),
                                           HttpMethod.GET,
                                           requestEntity,
                                           String::class.java)

      return convert(response.body!!)
    }
    catch (e: Exception) {

      return WimbledonErrorResponse
    }
  }

  private fun convert(body: String): WimbledonOkResponse =
      Gson().fromJson(body, WimbledonOkResponse::class.java)

  private fun composeUrl(baseUrl: String, year: Int): String =
      UriComponentsBuilder.fromHttpUrl(baseUrl)
          .path(TOURNAMENT_INFO_PATH)
          .buildAndExpand(year)
          .toString()

  companion object {

    private const val TOURNAMENT_INFO_PATH = "/en_GB/scores/feeds/{year}/draws/MS.json"
  }
}
