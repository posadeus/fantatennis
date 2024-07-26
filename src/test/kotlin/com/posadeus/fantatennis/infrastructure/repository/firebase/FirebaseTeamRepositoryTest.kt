package com.posadeus.fantatennis.infrastructure.repository.firebase

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.TeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.client.firebase.model.*
import com.posadeus.fantatennis.infrastructure.client.firebase.team.FirebasePlayerDao
import com.posadeus.fantatennis.infrastructure.client.firebase.team.FirebaseTeamDao
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FirebaseTeamRepositoryTest {

  private val firebaseTeamDao: FirebaseTeamDao = mockk()
  private val firebasePlayerDao: FirebasePlayerDao = mockk()

  private val repository: TeamRepository = FirebaseTeamRepository(firebaseTeamDao,
                                                                  firebasePlayerDao)

  @Test
  fun `team retrieved from client`() {


    val teamResponse = FoundFirebaseTeamResponse(A_TEAM_ID,
                                                 listOf(FirebaseTeamPlayerResponse("PLAYER_1_ID", true),
                                                        FirebaseTeamPlayerResponse("PLAYER_2_ID", false)))
    val firebasePlayer1Response = FirebasePlayerResponse(A_PLAYER_1_ATP_TOUR_ID, 12.0, "FULL_NAME_1")
    val firebasePlayer2Response = FirebasePlayerResponse(A_PLAYER_2_ATP_TOUR_ID, 22.2, "FULL_NAME_2")
    val firebasePlayer3Response = FirebasePlayerResponse(A_PLAYER_3_ATP_TOUR_ID, ANY_FANTA_POINTS, A_NAME)
    val players = mapOf("PLAYER_1_ID" to firebasePlayer1Response,
                        "PLAYER_2_ID" to firebasePlayer2Response,
                        PLAYER_3_ID to firebasePlayer3Response)
    val playersResponse = FirebasePlayersResponse(players)

    val player1 = TeamPlayerDto(fullName = "FULL_NAME_1",
                                fantaPoints = 12.0,
                                chosen = true)
    val player2 = TeamPlayerDto(fullName = "FULL_NAME_2",
                                fantaPoints = 22.2,
                                chosen = false)
    val expected = FoundTeam(TeamDto(players = listOf(player1, player2),
                                     totalScore = 34.2,
                                     completed = false))

    every { runBlocking { firebaseTeamDao.getTeam(A_TEAM_ID) } } returns teamResponse
    every { runBlocking { firebasePlayerDao.getAllPlayers() } } returns playersResponse

    runBlocking {
      assertThat(repository.getTeam(A_TEAM_ID)).isEqualTo(expected)
    }
  }

  @Test
  fun `team retrieved but is empty`() {

    val teamResponse = FoundFirebaseTeamResponse(A_TEAM_ID, emptyList())
    val expected = EmptyTeam

    every { runBlocking { firebaseTeamDao.getTeam(A_TEAM_ID) } } returns teamResponse

    runBlocking {
      assertThat(repository.getTeam(A_TEAM_ID)).isEqualTo(expected)
    }

    verify { firebasePlayerDao wasNot called }
  }

  @Test
  fun `team not found by client`() {

    val teamResponse = NotFoundFirebaseTeamResponse
    val expected = TeamIdNotFoundTeam

    every { runBlocking { firebaseTeamDao.getTeam(A_TEAM_ID) } } returns teamResponse

    runBlocking {
      assertThat(repository.getTeam(A_TEAM_ID)).isEqualTo(expected)
    }

    verify { firebasePlayerDao wasNot called }
  }

  @Test
  fun `error by client`() {

    val expected = ErrorTeam

    every { runBlocking { firebaseTeamDao.getTeam(A_TEAM_ID) } } throws Exception()

    runBlocking {
      assertThat(repository.getTeam(A_TEAM_ID)).isEqualTo(expected)
    }

    verify { firebasePlayerDao wasNot called }
  }

  companion object {

    private const val A_TEAM_ID = "A_TEAM_ID"
    private const val A_PLAYER_1_ATP_TOUR_ID = "AN_ATP_TOUR_ID_1"
    private const val A_PLAYER_2_ATP_TOUR_ID = "AN_ATP_TOUR_ID_2"
    private const val A_PLAYER_3_ATP_TOUR_ID = "AN_ATP_TOUR_ID_3"
    private const val PLAYER_3_ID = "PLAYER_3_ID"
    private const val A_NAME = "A_NAME"
    private const val ANY_FANTA_POINTS = 2.0
  }
}