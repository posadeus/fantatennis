package com.posadeus.fantatennis.infrastructure.repository.atptour

import com.posadeus.fantatennis.controller.model.ranking.RankedPlayer
import com.posadeus.fantatennis.domain.infrastructure.RankingRepository
import com.posadeus.fantatennis.domain.model.EmptyRanking
import com.posadeus.fantatennis.domain.model.RankedPlayers
import com.posadeus.fantatennis.infrastructure.client.atptour.AtpTourClient
import com.posadeus.fantatennis.infrastructure.client.atptour.model.*
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AtpTourRankingRepositoryTest {

  private val client: AtpTourClient = mockk()

  private val repository: RankingRepository = AtpTourRankingRepository(client)

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
                                                     fullName = "A_NAME",
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