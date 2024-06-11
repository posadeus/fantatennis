package com.posadeus.domain.service.ranking

import com.posadeus.controller.model.ranking.RankedPlayer
import com.posadeus.domain.infrastructure.AtpTourRepository
import com.posadeus.domain.model.EmptyRanking
import com.posadeus.domain.model.RankedPlayers
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RankingServiceTest {

  private val repository: AtpTourRepository = mockk()

  private val service = RankingService(repository)

  @Test
  fun `successful response from repository`() {

    every { repository.retrieveRanking(200) } returns RankedPlayers(PLAYERS)

    assertThat(service.retrieveRankedPlayer()).isEqualTo(RankedPlayers(PLAYERS))
  }

  @Test
  fun `error response from repository`() {

    every { repository.retrieveRanking(200) } returns EmptyRanking

    assertThat(service.retrieveRankedPlayer()).isEqualTo(EmptyRanking)
  }

  companion object {

    private val PLAYERS = emptyList<RankedPlayer>()
  }
}