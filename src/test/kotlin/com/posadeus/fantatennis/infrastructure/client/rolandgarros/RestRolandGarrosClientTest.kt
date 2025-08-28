package com.posadeus.fantatennis.infrastructure.client.rolandgarros

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.google.gson.Gson
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.impl.RestRolandGarrosClient
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosErrorResponse
import com.posadeus.fantatennis.infrastructure.client.rolandgarros.model.RolandGarrosOkResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.web.client.RestTemplate

class RestRolandGarrosClientTest {

  private val wireMock = WireMockServer(WireMockConfiguration.options().dynamicPort())

  private lateinit var client: RolandGarrosClient

  @BeforeEach
  fun setUp() {

    wireMock.start()

    val baseUrl = "http://localhost:" + wireMock.port()
    val restClient = RestTemplate()

    client = RestRolandGarrosClient(baseUrl, restClient)
  }

  @AfterEach
  fun tearDown() {

    wireMock.stop()
  }

  @Test
  fun `tournament provided successfully`() {

    val fileContent = this::class.java.classLoader.getResource("rolandGarrosResults.json")!!.readText()
    val expected = Gson().fromJson(fileContent, RolandGarrosOkResponse::class.java)

    wireMock.stubFor(
        get(urlPathEqualTo(PATH_PREFIX))
            .withQueryParam("year", equalTo(A_YEAR_TO_STRING))
            .withHeader("Accept", equalTo("application/json, text/plain, */*"))
            .withHeader("User-Agent", equalTo("*"))
            .willReturn(okJson(fileContent))
    )

    assertThat(client.retrieveDraws(A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `tournament endpoint generates error`() {

    val expected = RolandGarrosErrorResponse

    wireMock.stubFor(
        get(urlPathEqualTo(PATH_PREFIX))
            .withQueryParam("year", equalTo(A_YEAR_TO_STRING))
            .willReturn(serverError())
    )

    assertThat(client.retrieveDraws(A_YEAR)).isEqualTo(expected)
  }

  companion object {

    private const val PATH_PREFIX = "/api/en-us/results/SM"

    private const val A_YEAR = 2025
    private const val A_YEAR_TO_STRING = "2025"
  }
}