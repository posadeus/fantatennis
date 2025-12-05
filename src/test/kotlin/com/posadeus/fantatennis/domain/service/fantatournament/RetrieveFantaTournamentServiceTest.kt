package com.posadeus.fantatennis.domain.service.fantatournament

import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentDto
import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTournamentResultsRepository
import com.posadeus.fantatennis.domain.model.FoundFantaTournamentResults
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RetrieveFantaTournamentServiceTest {

  private val retrieveFantaTournamentResultsRepository: RetrieveFantaTournamentResultsRepository = mockk()

  private val service = RetrieveFantaTournamentService(retrieveFantaTournamentResultsRepository)

  @Test
  fun `retrieve tournament successfully`() {

    val tournamentDto = FantaTournamentDto(A_LIST_OF_TEAMS)
    val expected = FoundFantaTournamentResults(tournamentDto)

    every { retrieveFantaTournamentResultsRepository.retrieve(A_TOURNAMENT_ID) } returns expected

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TOURNAMENT_ID = 123

    private val A_LIST_OF_TEAMS = emptyList<TeamDto>()
  }
}