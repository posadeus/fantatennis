package com.posadeus.fantatennis.infrastructure.repository.dispatcher

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.tennistv.TennisTvTournamentInfoRepository
import com.posadeus.fantatennis.infrastructure.repository.wimbledon.WimbledonTournamentInfoRepository
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DispatcherTournamentInfoRepositoryTest {

  private val tennisTvRepository: TennisTvTournamentInfoRepository = mockk()
  private val wimbledonRepository: WimbledonTournamentInfoRepository = mockk()

  private val repository: TournamentInfoRepository = DispatcherTournamentInfoRepository(tennisTvRepository,
                                                                                        wimbledonRepository)

  @Test
  fun `dispatch to tennisTvRepository`() {

    val expected = CompleteTournamentInfo(A_TENNIS_TV_TOURNAMENT_ID, A_PARTICIPANTS, A_WINNERS)

    every { tennisTvRepository.retrieveTournamentInfo(A_TENNIS_TV_TOURNAMENT_ID, A_YEAR) } returns expected

    assertThat(repository.retrieveTournamentInfo(A_TENNIS_TV_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)

    verify { wimbledonRepository wasNot called }
  }

  @Test
  fun `dispatch to wimbledonRepository`() {

    val expected = CompleteTournamentInfo(39, A_PARTICIPANTS, A_WINNERS)

    every { wimbledonRepository.retrieveTournamentInfo(39, A_YEAR) } returns expected

    assertThat(repository.retrieveTournamentInfo(39, A_YEAR)).isEqualTo(expected)

    verify { tennisTvRepository wasNot called }
  }

  companion object {

    private const val A_TENNIS_TV_TOURNAMENT_ID = 123
    private const val A_YEAR = 2024

    private val A_PARTICIPANTS = emptySet<AtpPlayerId>()
    private val A_WINNERS = emptyMap<Round, Set<AtpPlayerId>>()
  }
}