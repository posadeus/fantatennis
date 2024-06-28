package com.posadeus.fantatennis.infrastructure.repository.team

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.TeamRepository
import com.posadeus.fantatennis.domain.model.EmptyTeam
import com.posadeus.fantatennis.domain.model.FoundTeam
import com.posadeus.fantatennis.infrastructure.client.firebase.FirebaseClient
import com.posadeus.fantatennis.infrastructure.client.firebase.model.TeamOkResponse
import com.posadeus.fantatennis.infrastructure.client.firebase.model.TeamPlayerResponse
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NoSqlTeamRepositoryTest {

  private val client: FirebaseClient = mockk()

  private val repository: TeamRepository = NoSqlTeamRepository(client)

  @Test
  fun `team retrieved from DB`() {

    val clientResponse = TeamOkResponse(userId = A_USER_ID,
                                        teamId = A_TEAM_ID,
                                        isTeamCompleted = false,
                                        players = listOf(TeamPlayerResponse(playerId = "PLAYER_1_ID",
                                                                            lastname = "FULL_NAME",
                                                                            firstName = "PLAYER_1",
                                                                            fantaPoints = 12,
                                                                            chosen = true,
                                                                            playing = "A_TOURNAMENT_ACTUALLY_PLAYED"),
                                                         TeamPlayerResponse(playerId = "PLAYER_2_ID",
                                                                            lastname = "FULL_NAME",
                                                                            firstName = "PLAYER_2",
                                                                            fantaPoints = 22,
                                                                            chosen = false,
                                                                            playing = null)))

    val player1 = TeamPlayerDto(fullName = "PLAYER_1 FULL_NAME",
                                fantaPoints = 12,
                                chosen = true,
                                playing = "A_TOURNAMENT_ACTUALLY_PLAYED")
    val player2 = TeamPlayerDto(fullName = "PLAYER_2 FULL_NAME",
                                fantaPoints = 22,
                                chosen = false,
                                playing = null)
    val expected = FoundTeam(TeamDto(players = listOf(player1, player2),
                                     totalScore = 34,
                                     completed = false))

    every { client.retrieveTeam(A_USER_ID, A_TEAM_ID) } returns clientResponse

    assertThat(repository.getTeam(A_USER_ID, A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `team retrieved but is empty`() {

    val clientResponse = TeamOkResponse(userId = A_USER_ID,
                                        teamId = A_TEAM_ID,
                                        isTeamCompleted = false,
                                        players = null)

    val expected = EmptyTeam

    every { client.retrieveTeam(A_USER_ID, A_TEAM_ID) } returns clientResponse

    assertThat(repository.getTeam(A_USER_ID, A_TEAM_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_USER_ID = 1234L
    private const val A_TEAM_ID = "A_TEAM_ID"
  }
}