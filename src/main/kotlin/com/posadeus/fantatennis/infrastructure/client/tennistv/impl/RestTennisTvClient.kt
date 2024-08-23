package com.posadeus.fantatennis.infrastructure.client.tennistv.impl

import com.google.gson.Gson
import com.posadeus.fantatennis.infrastructure.client.tennistv.TennisTvClient
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.*
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

class RestTennisTvClient(private val baseUrl: String,
                         private val restTemplate: RestTemplate) : TennisTvClient {

  override fun retrieveTournamentInfo(tournamentId: Int, year: Int): TennisTvTournamentResponse {

    try {

      val headers = HttpHeaders()
      headers.accept = listOf(MediaType.APPLICATION_JSON, MediaType(MediaType.TEXT_PLAIN), MediaType(MediaType.ALL))
      headers.add(HttpHeaders.USER_AGENT, "*")

      val requestEntity: HttpEntity<String> = HttpEntity(headers)

      val response = restTemplate.exchange(composeUrl(baseUrl, tournamentId, year),
                                           HttpMethod.GET,
                                           requestEntity,
                                           String::class.java)

      return convert(response.body!!)
    }
    catch (e: Exception) {

      return TennisTvTournamentErrorResponse
    }
  }

  private fun convert(body: String): TennisTvTournamentOkResponse =
      Gson().fromJson(body, TournamentResponse::class.java)
          .let(::TennisTvTournamentOkResponse)

  private fun composeUrl(baseUrl: String, tournamentId: Int, year: Int): String =
      UriComponentsBuilder.fromHttpUrl(baseUrl)
          .path(TOURNAMENT_INFO_PATH)
          .buildAndExpand(tournamentId, year)
          .toString()

  companion object {

    private const val TOURNAMENT_INFO_PATH = "/v1/tournaments/{tournamentId}/{year}/draws"
  }
}
