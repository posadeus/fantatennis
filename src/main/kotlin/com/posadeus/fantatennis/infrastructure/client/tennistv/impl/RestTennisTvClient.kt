package com.posadeus.fantatennis.infrastructure.client.tennistv.impl

import com.google.common.reflect.TypeToken
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

      val response = restTemplate.exchange(composeUrl(baseUrl, TOURNAMENT_INFO_PATH, tournamentId, year),
                                           HttpMethod.GET,
                                           requestEntity,
                                           String::class.java)

      return toTennisTvTournamentOkResponse(response.body!!)
    }
    catch (e: Exception) {

      return TennisTvTournamentErrorResponse
    }
  }

  override fun retrieveTournamentsRegistry(year: Int): TennisTvTournamentsRegistryResponse {

    try {

      val headers = HttpHeaders()
      headers.accept = listOf(MediaType.APPLICATION_JSON, MediaType(MediaType.TEXT_PLAIN), MediaType(MediaType.ALL))
      headers.add(HttpHeaders.USER_AGENT, "*")

      val requestEntity: HttpEntity<String> = HttpEntity(headers)

      val response = restTemplate.exchange(composeUrl(baseUrl, TOURNAMENTS_REGISTRY_PATH, "$year-01-01", "$year-12-31"),
                                           HttpMethod.GET,
                                           requestEntity,
                                           String::class.java)

      return toTennisTvTournamentsRegistryResponse(response.body!!)
    }
    catch (e: Exception) {

      return TennisTvTournamentsRegistryResponse(tournaments = emptyList())
    }
  }

  private fun composeUrl(baseUrl: String, path: String, vararg params: Any): String =
      UriComponentsBuilder.fromHttpUrl(baseUrl)
          .path(path)
          .buildAndExpand(*params)
          .toString()

  private fun toTennisTvTournamentOkResponse(body: String): TennisTvTournamentOkResponse =
      Gson().fromJson(body, TournamentResponse::class.java)
          .let(::TennisTvTournamentOkResponse)

  private fun toTennisTvTournamentsRegistryResponse(body: String): TennisTvTournamentsRegistryResponse =
      Gson().fromJson<List<TennisTvTournamentRegistry>?>(body, object : TypeToken<List<TennisTvTournamentRegistry>>() {}.type)
          .let(::TennisTvTournamentsRegistryResponse)

  companion object {

    private const val TOURNAMENT_INFO_PATH = "/v1/tournaments/{tournamentId}/{year}/draws"
    private const val TOURNAMENTS_REGISTRY_PATH = "/v1/tournaments?from={from}&to={to}&size=250"
  }
}
