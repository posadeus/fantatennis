package com.posadeus.fantatennis.infrastructure.client.usopen.impl

import com.google.gson.Gson
import com.posadeus.fantatennis.infrastructure.client.usopen.UsOpenClient
import com.posadeus.fantatennis.infrastructure.client.usopen.model.*
import org.springframework.http.*
import org.springframework.http.MediaType.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

class RestUsOpenClient(private val baseUrl: String,
                       private val restTemplate: RestTemplate) : UsOpenClient {

  override fun retrieveDraws(year: Int): UsOpenResponse {

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

      return UsOpenErrorResponse
    }
  }

  private fun convert(body: String): UsOpenOkResponse =
      Gson().fromJson(body, UsOpenOkResponse::class.java)

  private fun composeUrl(baseUrl: String, year: Int): String =
      UriComponentsBuilder.fromHttpUrl(baseUrl)
          .path(TOURNAMENT_INFO_PATH)
          .buildAndExpand(year)
          .toString()

  companion object {

    private const val TOURNAMENT_INFO_PATH = "/en_US/scores/feeds/{year}/draws/MS.json"
  }
}
