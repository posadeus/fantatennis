package com.posadeus.infrastructure.client.atptour

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.posadeus.fantatennis.infrastructure.client.atptour.AtpTourClient
import com.posadeus.fantatennis.infrastructure.client.atptour.impl.RestAtpTourClient
import com.posadeus.fantatennis.infrastructure.client.atptour.model.AtpTourRankingErrorResponse
import com.posadeus.fantatennis.infrastructure.client.atptour.model.AtpTourRankingsOkResponse
import com.posadeus.infrastructure.client.atptour.model.AtpTourRankingOkResponseBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.web.client.RestTemplate

class RestAtpTourClientTest {

  private val wireMock = WireMockServer(WireMockConfiguration.options().dynamicPort())

  private lateinit var client: AtpTourClient

  @BeforeEach
  fun setUp() {

    wireMock.start()

    val baseUrl = "http://localhost:" + wireMock.port()
    val restClient = RestTemplate()

    client = RestAtpTourClient(baseUrl, restClient)
  }

  @AfterEach
  fun tearDown() {

    wireMock.stop()
  }

  @Test
  fun `ranking provided successfully`() {

    val positions = 2

    val element1 = AtpTourRankingOkResponseBuilder()
        .withRankNo(1)
        .withName("Novak Djokovic")
        .withPoints("9,960")
        .withUrlHeadshotImage("/-/media/alias/player-headshot/d643")
        .withUrlCountryFlag("/-/media/images/flags/srb.svg")
        .withMovement(0)
        .withCountry("Serbia")
        .withCountryCode("SRB")
        .withPlayerId("D643")
        .withPlayerProfileUrl("/en/players/novak-djokovic/d643/overview")
        .build()
    val element2 = AtpTourRankingOkResponseBuilder()
        .withRankNo(2)
        .withName("Jannik Sinner")
        .withPoints("8,770")
        .withUrlHeadshotImage("/-/media/alias/player-headshot/s0ag")
        .withUrlCountryFlag("/-/media/images/flags/ita.svg")
        .withMovement(0)
        .withCountry("Italy")
        .withCountryCode("ITA")
        .withPlayerId("S0AG")
        .withPlayerProfileUrl("/en/players/jannik-sinner/s0ag/overview")
        .build()
    val expected = AtpTourRankingsOkResponse(listOf(element1, element2))

    wireMock.stubFor(get(urlPathEqualTo("$PATH$positions"))
                         .withQueryParam("v", equalTo("1"))
                         .withHeader("Accept", equalTo("application/json, text/plain, */*"))
                         .withHeader("User-Agent", equalTo("*"))
                         .willReturn(okJson(RANKING_2_OK_RESPONSE)))

    assertThat(client.retrieveRanking(positions)).isEqualTo(expected)
  }

  @Test
  fun `ranking endpoint generates error`() {

    val expected = AtpTourRankingErrorResponse

    wireMock.stubFor(get(urlPathEqualTo("$PATH$ANY_POSITION"))
                         .withQueryParam("v", equalTo("1"))
                         .willReturn(serverError()))

    assertThat(client.retrieveRanking(ANY_POSITION)).isEqualTo(expected)
  }

  companion object {

    private const val PATH = "/en/-/www/rank/sglroll/"
    private const val ANY_POSITION = 1

    private const val RANKING_2_OK_RESPONSE =
        """
          [
    {
        "RankNo": 1,
        "Name": "Novak Djokovic",
        "Points": "9,960",
        "UrlHeadshotImage": "/-/media/alias/player-headshot/d643",
        "UrlCountryFlag": "/-/media/images/flags/srb.svg",
        "Movement": 0,
        "Country": "Serbia",
        "CountryCode": "SRB",
        "PlayerId": "D643",
        "PlayerProfileUrl": "/en/players/novak-djokovic/d643/overview"
    },
    {
        "RankNo": 2,
        "Name": "Jannik Sinner",
        "Points": "8,770",
        "UrlHeadshotImage": "/-/media/alias/player-headshot/s0ag",
        "UrlCountryFlag": "/-/media/images/flags/ita.svg",
        "Movement": 0,
        "Country": "Italy",
        "CountryCode": "ITA",
        "PlayerId": "S0AG",
        "PlayerProfileUrl": "/en/players/jannik-sinner/s0ag/overview"
    }
    ]
        """
  }
}