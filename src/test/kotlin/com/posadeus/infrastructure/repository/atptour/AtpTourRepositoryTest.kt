package com.posadeus.infrastructure.repository.atptour

import com.posadeus.controller.model.ranking.RankedPlayer
import com.posadeus.domain.infrastructure.AtpTourRepository
import com.posadeus.domain.model.EmptyRanking
import com.posadeus.domain.model.RankedPlayers
import com.posadeus.infrastructure.client.atptour.AtpTourClient
import com.posadeus.infrastructure.client.atptour.model.AtpTourRankingErrorResponse
import com.posadeus.infrastructure.client.atptour.model.AtpTourRankingOkResponse
import com.posadeus.infrastructure.client.atptour.model.AtpTourRankingsOkResponse
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AtpTourRepositoryTest {

  private val client: AtpTourClient = mockk()

  private val repository: AtpTourRepository = AtpTourRepositoryImpl(client)

  @Test
  fun `client success, returned converted successful response`() {

    val clientResponse = AtpTourRankingsOkResponse(listOf(AtpTourRankingOkResponse(rankNo = 1,
                                                                                   name = "A_NAME",
                                                                                   points = "10,000",
                                                                                   urlHeadshotImage = "",
                                                                                   urlCountryFlag = "",
                                                                                   movement = 0,
                                                                                   country = "A_COUNTRY",
                                                                                   countryCode = "AC",
                                                                                   playerId = "AN_ID",
                                                                                   playerProfileUrl = "")))
    val expected = RankedPlayers(listOf(RankedPlayer(id = "AN_ID",
                                                     name = "A_NAME",
                                                     surname = "A_NAME",
                                                     age = 0,
                                                     rank = 1,
                                                     points = 10000)))

    every { client.retrieveRanking(ANY_POSITIONS) } returns clientResponse

    assertThat(repository.retrieveRanking(ANY_POSITIONS)).isEqualTo(expected)
  }

  @Test
  fun `client returns error, returned converted error response`() {

    val clientResponse = AtpTourRankingErrorResponse
    val expected = EmptyRanking

    every { client.retrieveRanking(ANY_POSITIONS) } returns clientResponse

    assertThat(repository.retrieveRanking(ANY_POSITIONS)).isEqualTo(expected)
  }

  companion object {

    private const val ANY_POSITIONS = 10
  }
}