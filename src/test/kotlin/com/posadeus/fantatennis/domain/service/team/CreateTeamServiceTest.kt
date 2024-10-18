package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamCreatedDto
import com.posadeus.fantatennis.controller.model.team.TeamToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.domain.model.*
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CreateTeamServiceTest {

  private val repository: FantaTeamsRepository = mockk()

  private val service = CreateTeamService(repository)

  @Test
  fun `creation successful`() {

    val ownerId = "OWNER_ID"
    val request = TeamToCreateDto(ownerId = ownerId)
    val fantaTeam = FantaTeam(id = 1, ownerId = ownerId)

    val expected: TeamCreation = TeamCreated(team = TeamCreatedDto(id = 1, ownerId = ownerId))

    every { repository.createTeam(ownerId) } returns fantaTeam

    assertThat(service.create(request)).isEqualTo(expected)
  }
}


