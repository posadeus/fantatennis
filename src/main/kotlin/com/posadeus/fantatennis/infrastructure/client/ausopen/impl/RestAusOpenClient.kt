package com.posadeus.fantatennis.infrastructure.client.ausopen.impl

import com.google.gson.Gson
import com.posadeus.fantatennis.infrastructure.client.ausopen.AusOpenClient
import com.posadeus.fantatennis.infrastructure.client.ausopen.model.*
import org.springframework.http.*
import org.springframework.http.MediaType.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

class RestAusOpenClient(private val baseUrl: String,
                        private val restTemplate: RestTemplate) : AusOpenClient {

  override fun retrieveDraws(eventNid: Int): AusOpenResponse {

    try {

      val headers = HttpHeaders()
      headers.accept = listOf(APPLICATION_JSON, MediaType(TEXT_PLAIN), MediaType(ALL))
      headers.add(HttpHeaders.USER_AGENT, "*")

      val requestEntity: HttpEntity<String> = HttpEntity(headers)

      val response = restTemplate.exchange(composeUrl(baseUrl, eventNid),
                                           HttpMethod.GET,
                                           requestEntity,
                                           String::class.java)

      return convert(response.body!!)
    }
    catch (e: Exception) {

      return AusOpenErrorResponse
    }
  }

  private fun convert(body: String): AusOpenOkResponse =
      Gson().fromJson(body, AusOpenOkResponse::class.java)

  private fun composeUrl(baseUrl: String, eventNid: Int): String =
      UriComponentsBuilder.fromHttpUrl(baseUrl)
          .path(TOURNAMENT_INFO_PATH)
          .buildAndExpand(eventNid)
          .toString()

  companion object {

    private const val TOURNAMENT_INFO_PATH = "/event/{eventNid}/draws"
  }
}
