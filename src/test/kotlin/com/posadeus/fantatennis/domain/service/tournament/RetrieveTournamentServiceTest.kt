package com.posadeus.fantatennis.domain.service.tournament

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.tournament.TournamentDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FoundFantaTournamentResults
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RetrieveTournamentServiceTest {

  private val fantaTournamentsRepository: FantaTournamentsRepository = mockk()

  private val service = RetrieveTournamentService(fantaTournamentsRepository)

  @Test
  fun `retrieve tournament successfully`() {

    val tournamentDto = TournamentDto(A_LIST_OF_TEAMS)
    val expected = FoundFantaTournamentResults(tournamentDto)

    every { fantaTournamentsRepository.retrieveTournamentResults(A_TOURNAMENT_ID) } returns expected

    assertThat(service.retrieve(A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TOURNAMENT_ID = 123

    private val A_LIST_OF_TEAMS = emptyList<TeamDto>()
  }
}