package com.posadeus.fantatennis.domain.service.team

import com.posadeus.fantatennis.controller.model.team.TeamCreatedDto
import com.posadeus.fantatennis.controller.model.team.TeamToCreateDto
import com.posadeus.fantatennis.domain.exception.FantaTeamCreationException
import com.posadeus.fantatennis.domain.infrastructure.CreateTeamRepository
import com.posadeus.fantatennis.domain.infrastructure.CurrentUser
import com.posadeus.fantatennis.domain.model.*
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CreateTeamServiceTest {

  private val createTeamRepository: CreateTeamRepository = mockk()
  private val currentUser: CurrentUser = mockk()

  private val service = CreateTeamService(createTeamRepository, currentUser)

  @Test
  fun `creation succeeds using the authenticated user as owner`() {

    val dto = TeamToCreateDto(tournamentId = 100)

    val fantaTeam = FantaTeam(id = 1, ownerId = AN_OWNER_ID)

    val expected: TeamCreation = TeamCreated(team = TeamCreatedDto(id = 1, ownerId = AN_OWNER_ID))

    every { currentUser.ownerId() } returns AN_OWNER_ID
    every { createTeamRepository.create(AN_OWNER_ID, 100) } returns fantaTeam

    assertThat(service.create(dto)).isEqualTo(expected)

    verify(exactly = 1) { createTeamRepository.create(AN_OWNER_ID, 100) }
  }

  @Test
  fun `creation fails due to an error from repository`() {

    val request = TeamToCreateDto(tournamentId = A_TOURNAMENT_ID)

    val expected = ErrorTeamCreation

    every { currentUser.ownerId() } returns AN_OWNER_ID
    every { createTeamRepository.create(AN_OWNER_ID, A_TOURNAMENT_ID) } throws FantaTeamCreationException("Scary error")

    assertThat(service.create(request)).isEqualTo(expected)
  }

  @Test
  fun `creation fails when there is no authenticated user`() {

    val request = TeamToCreateDto(tournamentId = A_TOURNAMENT_ID)

    val expected = ErrorTeamCreation

    every { currentUser.ownerId() } throws IllegalStateException("No authenticated user with an email in the security context")

    assertThat(service.create(request)).isEqualTo(expected)

    verify { createTeamRepository wasNot called }
  }

  companion object {

    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val A_TOURNAMENT_ID = 100
  }
}


