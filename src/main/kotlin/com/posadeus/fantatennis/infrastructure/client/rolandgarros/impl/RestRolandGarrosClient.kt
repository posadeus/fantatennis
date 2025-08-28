package com.posadeus.fantatennis.infrastructure.client.rolandgarros.impl

import com.google.gson.Gson
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.RolandGarrosClient
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.*
import org.springframework.http.*
import org.springframework.http.MediaType.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder.fromHttpUrl

class RestRolandGarrosClient(private val baseUrl: String,
                             private val restTemplate: RestTemplate) : RolandGarrosClient {

  override fun retrieveDraws(year: Int): RolandGarrosResponse {

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

      return RolandGarrosErrorResponse
    }
  }

  private fun convert(body: String): RolandGarrosOkResponse =
      Gson().fromJson(body, RolandGarrosOkResponse::class.java)

  private fun composeUrl(baseUrl: String, year: Int): String =
      fromHttpUrl(baseUrl)
          .path(TOURNAMENT_INFO_PATH)
          .queryParam("year", year)
          .build()
          .toString()

  companion object {

    private const val TOURNAMENT_INFO_PATH = "/api/en-us/results/SM"
  }
}
