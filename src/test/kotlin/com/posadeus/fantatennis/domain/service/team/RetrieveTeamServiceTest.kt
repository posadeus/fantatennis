package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.model.*
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RetrieveTeamServiceTest {

  private val retrieveFantaTeamRepository: RetrieveFantaTeamRepository = mockk()

  private val service = RetrieveTeamService(retrieveFantaTeamRepository)

  @Test
  fun `team not found due to negative response from the repository`() {

    val expected = TeamIdNotFoundTeam

    every { retrieveFantaTeamRepository.retrieve(A_TEAM_ID) } returns expected

    assertThat(service.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `error from the repository`() {

    val expected = ErrorTeam

    every { retrieveFantaTeamRepository.retrieve(A_TEAM_ID) } returns expected

    assertThat(service.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `team found`() {

    val teamPlayerDto1 = TeamPlayerDto(fullName = "A_PLAYER_NAME_1", fantaPoints = 20.0)
    val teamPlayerDto2 = TeamPlayerDto(fullName = "A_PLAYER_NAME_2", fantaPoints = 18.0)
    val expected = FoundTeam(TeamDto(owner = "", listOf(teamPlayerDto1, teamPlayerDto2), 38.0))

    every { retrieveFantaTeamRepository.retrieve(A_TEAM_ID) } returns expected

    assertThat(service.retrieve(A_TEAM_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TEAM_ID = 1
  }
}