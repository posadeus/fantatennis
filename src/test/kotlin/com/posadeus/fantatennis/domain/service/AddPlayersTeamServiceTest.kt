package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.domain.model.FoundTeam
import com.posadeus.fantatennis.infrastructure.TeamsRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AddPlayersTeamServiceTest {

  private val teamsRepository: TeamsRepository = mockk()

  private val service = AddPlayersTeamService(teamsRepository)

  @Test
  fun `add players correctly`() {

    val domainPlayers = setOf(DomainPlayer(id = A_PLAYER_ID,
                                           atpId = AN_ATP_PLAYER_ID,
                                           fullName = A_PLAYER_FULL_NAME))

    val expected = FoundTeam(team = TeamDto(players = listOf(TeamPlayerDto(fullName = A_PLAYER_FULL_NAME,
                                                                           fantaPoints = 0.0)),
                                            totalScore = 0.0))

    every { teamsRepository.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID)) } returns domainPlayers

    assertThat(service.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID))).isEqualTo(expected)
  }

  companion object {

    private const val A_TEAM_ID = 1
    private const val A_PLAYER_FULL_NAME = "A_PLAYER_FULL_NAME"
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val AN_ATP_PLAYER_ID = "AN_ATP_PLAYER_ID"
  }
}