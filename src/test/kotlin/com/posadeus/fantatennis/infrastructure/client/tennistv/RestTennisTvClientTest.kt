package com.posadeus.fantatennis.infrastructure.client.tennistv

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.google.gson.Gson
import com.posadeus.fantatennis.infrastructure.client.tennistv.impl.RestTennisTvClient
import com.posadeus.fantatennis.infrastructure.client.tennistv.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.web.client.RestTemplate

class RestTennisTvClientTest {

  private val wireMock = WireMockServer(WireMockConfiguration.options().dynamicPort())

  private lateinit var client: TennisTvClient

  @BeforeEach
  fun setUp() {

    wireMock.start()

    val baseUrl = "http://localhost:" + wireMock.port()
    val restClient = RestTemplate()

    client = RestTennisTvClient(baseUrl, restClient)
  }

  @AfterEach
  fun tearDown() {

    wireMock.stop()
  }

  @Test
  fun `tournament provided successfully`() {

    val fileContent = this::class.java.classLoader.getResource("tournamentResults.json")!!.readText()
    val expected = TennisTvTournamentOkResponse(Gson().fromJson(fileContent, TournamentResponse::class.java))

    wireMock.stubFor(
      get(urlPathEqualTo("$PATH_PREFIX$A_TOURNAMENT_ID/$A_YEAR$PATH_SUFFIX"))
        .withHeader("Accept", equalTo("application/json, text/plain, */*"))
        .withHeader("User-Agent", equalTo("*"))
        .willReturn(okJson(fileContent))
    )

    assertThat(client.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `tournament endpoint generates error`() {

    val expected = TennisTvTournamentErrorResponse

    wireMock.stubFor(
      get(urlPathEqualTo("$PATH_PREFIX$A_TOURNAMENT_ID/$A_YEAR$PATH_SUFFIX"))
        .withQueryParam("v", equalTo("1"))
        .willReturn(serverError())
    )

    assertThat(client.retrieveTournamentInfo(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  companion object {

    private const val PATH_PREFIX = "/v1/tournaments/"
    private const val PATH_SUFFIX = "/draws"

    private const val A_TOURNAMENT_ID = 422
    private const val A_YEAR = 2024
  }
}