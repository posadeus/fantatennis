package com.posadeus.fantatennis.infrastructure.client.atptour.impl

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
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

      return try {

        fallbackOnJSONBackup()
      }
      catch (e: Exception) {

        AtpTourRankingErrorResponse
      }
    }
  }

  private fun fallbackOnJSONBackup(): AtpTourRankingsOkResponse {
    
    val fileContent = this::class.java.classLoader.getResource("backupAtpRanking.json")!!.readText()
    val listType = object : TypeToken<List<AtpTourRankingOkResponse>>() {}.type
    val myList: List<AtpTourRankingOkResponse> = Gson().fromJson(fileContent, listType)
    
    return myList.let(::AtpTourRankingsOkResponse)
  }

  private fun convert(body: Array<*>): AtpTourRankingsOkResponse =
      body.map { it as Map<String, Any> }
          .map {
            AtpTourRankingOkResponse(RankNo = it["RankNo"].toString().toInt(),
                                     Name = it["Name"].toString(),
                                     Points = it["Points"].toString(),
                                     UrlHeadshotImage = it["UrlHeadshotImage"].toString(),
                                     UrlCountryFlag = it["UrlCountryFlag"].toString(),
                                     Movement = it["Movement"].toString().toInt(),
                                     Country = it["Country"].toString(),
                                     CountryCode = it["CountryCode"].toString(),
                                     PlayerId = it["PlayerId"].toString(),
                                     PlayerProfileUrl = it["PlayerProfileUrl"].toString())
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
