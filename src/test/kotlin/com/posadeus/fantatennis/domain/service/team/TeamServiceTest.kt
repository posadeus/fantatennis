package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.domain.infrastructure.TeamRepository
import com.posadeus.fantatennis.domain.model.FoundTeam
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TeamServiceTest {

  private val repository: TeamRepository = mockk()

  private val service = TeamService(repository)

  @Test
  fun `retrieve a team`() {

    every { repository.getTeam(A_USER_ID, A_TEAM_ID) } returns A_TEAM

    assertThat(service.getTeam(A_USER_ID, A_TEAM_ID)).isEqualTo(A_TEAM)
  }

  companion object {

    private const val A_USER_ID = "A_USER_ID"
    private const val A_TEAM_ID = "A_TEAM_ID"

    private val A_TEAM = FoundTeam(TeamDto(emptyList()))
  }
}