package com.posadeus.fantatennis.infrastructure.client.usopen

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.google.gson.Gson
import com.posadeus.fantatennis.infrastructure.client.usopen.impl.RestUsOpenClient
import com.posadeus.fantatennis.infrastructure.client.usopen.model.UsOpenErrorResponse
import com.posadeus.fantatennis.infrastructure.client.usopen.model.UsOpenOkResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.web.client.RestTemplate

class RestUsOpenClientTest {

  private val wireMock = WireMockServer(WireMockConfiguration.options().dynamicPort())

  private lateinit var client: UsOpenClient

  @BeforeEach
  fun setUp() {

    wireMock.start()

    val baseUrl = "http://localhost:" + wireMock.port()
    val restClient = RestTemplate()

    client = RestUsOpenClient(baseUrl, restClient)
  }

  @AfterEach
  fun tearDown() {

    wireMock.stop()
  }

  @Test
  fun `tournament provided successfully`() {

    val fileContent = this::class.java.classLoader.getResource("usOpenResults.json")!!.readText()
    val expected = Gson().fromJson(fileContent, UsOpenOkResponse::class.java)

    wireMock.stubFor(
        get(urlPathEqualTo("$PATH_PREFIX$A_YEAR$PATH_SUFFIX"))
            .withHeader("Accept", equalTo("application/json, text/plain, */*"))
            .withHeader("User-Agent", equalTo("*"))
            .willReturn(okJson(fileContent))
    )

    assertThat(client.retrieveDraws(A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `tournament endpoint generates error`() {

    val expected = UsOpenErrorResponse

    wireMock.stubFor(
        get(urlPathEqualTo("$PATH_PREFIX$A_YEAR$PATH_SUFFIX"))
            .withQueryParam("v", equalTo("1"))
            .willReturn(serverError())
    )

    assertThat(client.retrieveDraws(A_YEAR)).isEqualTo(expected)
  }

  companion object {

    private const val PATH_PREFIX = "/en_US/scores/feeds/"
    private const val PATH_SUFFIX = "/draws/MS.json"

    private const val A_YEAR = 2024
  }
}