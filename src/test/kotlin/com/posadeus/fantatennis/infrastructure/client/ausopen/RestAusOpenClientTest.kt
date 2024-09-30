package com.posadeus.fantatennis.infrastructure.client.ausopen

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.google.gson.Gson
import com.posadeus.fantatennis.infrastructure.client.ausopen.impl.RestAusOpenClient
import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenErrorResponse
import com.posadeus.fantatennis.infrastructure.client.ausopen.model.AusOpenOkResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.web.client.RestTemplate

class RestAusOpenClientTest {

  private val wireMock = WireMockServer(WireMockConfiguration.options().dynamicPort())

  private lateinit var client: AusOpenClient

  @BeforeEach
  fun setUp() {

    wireMock.start()

    val baseUrl = "http://localhost:" + wireMock.port()
    val restClient = RestTemplate()

    client = RestAusOpenClient(baseUrl, restClient)
  }

  @AfterEach
  fun tearDown() {

    wireMock.stop()
  }

  @Test
  fun `tournament provided successfully`() {

    val fileContent = this::class.java.classLoader.getResource("ausOpenResults.json")!!.readText()
    val expected = Gson().fromJson(fileContent, AusOpenOkResponse::class.java)

    wireMock.stubFor(
        get(urlPathEqualTo("$PATH_PREFIX$AN_EVENT_NID$PATH_SUFFIX"))
            .withHeader("Accept", equalTo("application/json, text/plain, */*"))
            .withHeader("User-Agent", equalTo("*"))
            .willReturn(okJson(fileContent))
    )

    assertThat(client.retrieveDraws(AN_EVENT_NID)).isEqualTo(expected)
  }

  @Test
  fun `tournament endpoint generates error`() {

    val expected = AusOpenErrorResponse

    wireMock.stubFor(
        get(urlPathEqualTo("$PATH_PREFIX$AN_EVENT_NID$PATH_SUFFIX"))
            .willReturn(serverError())
    )

    assertThat(client.retrieveDraws(AN_EVENT_NID)).isEqualTo(expected)
  }

  companion object {

    private const val PATH_PREFIX = "/event/"
    private const val PATH_SUFFIX = "/draws"

    private const val AN_EVENT_NID = 245421
  }
}