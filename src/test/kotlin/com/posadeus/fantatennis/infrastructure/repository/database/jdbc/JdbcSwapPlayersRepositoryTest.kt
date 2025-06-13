package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.team.PlayersToSwapDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.TeamIdNotFoundTeam
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class JdbcSwapPlayersRepositoryTest {

  private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository = mockk()

  private val repository: SwapPlayersRepository = JdbcSwapPlayersRepository(retrieveFantaTeamRepository)

  @Test
  fun `swap fails due to team not found`() {

    val expected = TeamIdNotFoundTeam

    every { retrieveFantaTeamRepository.retrieve(A_NOT_EXISTING_TEAM_ID) } returns TeamIdNotFoundTeam

    assertThat(repository.swap(A_NOT_EXISTING_TEAM_ID, ANY_SWAP_PLAYERS)).isEqualTo(expected)
  }

  companion object {

    private const val A_NOT_EXISTING_TEAM_ID = 1

    private val ANY_SWAP_PLAYERS = PlayersToSwapDto()
  }
}