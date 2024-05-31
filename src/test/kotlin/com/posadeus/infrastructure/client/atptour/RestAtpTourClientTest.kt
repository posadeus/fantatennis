package com.posadeus.infrastructure.client.atptour

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.posadeus.infrastructure.client.atptour.impl.RestAtpTourClient
import com.posadeus.infrastructure.client.atptour.model.AtpTourRankingResponseBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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

    val element1 = AtpTourRankingResponseBuilder()
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
    val element2 = AtpTourRankingResponseBuilder()
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
    val expected = listOf(element1, element2)

    wireMock.stubFor(get(urlPathEqualTo("$PATH$positions"))
                         .withQueryParam("v", equalTo("1"))
                         .willReturn(okJson(RANKING_2_OK_RESPONSE)))

    assertThat(client.retrieveRanking(positions)).isEqualTo(expected)
  }

  companion object {

    private const val PATH = "/en/-/www/rank/sglroll/"

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