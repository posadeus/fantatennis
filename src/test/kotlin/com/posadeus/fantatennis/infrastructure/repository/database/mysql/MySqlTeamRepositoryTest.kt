package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.infrastructure.TeamRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.MySqlTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MySqlTeamRepositoryTest {

  private val mySqlTeamDao: MySqlTeamDao = mockk()
  private val playersDao: PlayersDao = mockk()

  private val repository: TeamRepository = MySqlTeamRepository(mySqlTeamDao,
                                                               playersDao)

  @Test
  fun `team retrieved from DB`() {

    val teamResponse = listOf(TeamEntity(id = TeamKeyEmbedded(teamId = A_TEAM_ID, playerId = A_PLAYER_ID_1),
                                         chosen = true),
                              TeamEntity(id = TeamKeyEmbedded(teamId = A_TEAM_ID, playerId = A_PLAYER_ID_2),
                                         chosen = false))
    val playersResponse = listOf(PlayerEntity(id = A_PLAYER_ID_1,
                                              atpTourId = AN_ATP_TOUR_ID_1,
                                              fantaPoints = 12.0,
                                              fullName = A_FULL_NAME_1),
                                 PlayerEntity(id = A_PLAYER_ID_2,
                                              atpTourId = AN_ATP_TOUR_ID_2,
                                              fantaPoints = 22.2,
                                              fullName = A_FULL_NAME_2))

    val player1 = TeamPlayerDto(fullName = A_FULL_NAME_1,
                                fantaPoints = 12.0,
                                chosen = true)
    val player2 = TeamPlayerDto(fullName = A_FULL_NAME_2,
                                fantaPoints = 22.2,
                                chosen = false)
    val expected = FoundTeam(team = TeamDto(players = listOf(player1, player2),
                                            totalScore = 34.2,
                                            completed = false))

    every { mySqlTeamDao.findByIdTeamId(A_TEAM_ID) } returns teamResponse
    every { playersDao.findAllById(setOf(A_PLAYER_ID_1, A_PLAYER_ID_2)) } returns playersResponse

    assertThat(repository.getTeam(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `teamResponse is empty`() {

    val teamResponse = emptyList<TeamEntity>()
    val expected = EmptyTeam

    every { mySqlTeamDao.findByIdTeamId(A_TEAM_ID) } returns teamResponse

    assertThat(repository.getTeam(A_TEAM_ID)).isEqualTo(expected)

    verify { playersDao wasNot called }
  }

  @Test
  fun `playersResponse is empty`() {

    val teamResponse = listOf(TeamEntity(id = TeamKeyEmbedded(teamId = A_TEAM_ID, playerId = A_PLAYER_ID_1),
                                         chosen = true),
                              TeamEntity(id = TeamKeyEmbedded(teamId = A_TEAM_ID, playerId = A_PLAYER_ID_2),
                                         chosen = false))
    val expected = ErrorTeam

    every { mySqlTeamDao.findByIdTeamId(A_TEAM_ID) } returns teamResponse
    every { playersDao.findAllById(setOf(A_PLAYER_ID_1, A_PLAYER_ID_2)) } returns emptyList()

    assertThat(repository.getTeam(A_TEAM_ID)).isEqualTo(expected)
  }

  @Test
  fun `playersResponse doesn't contains all the players`() {

    val teamResponse = listOf(TeamEntity(id = TeamKeyEmbedded(teamId = A_TEAM_ID, playerId = A_PLAYER_ID_1),
                                         chosen = true),
                              TeamEntity(id = TeamKeyEmbedded(teamId = A_TEAM_ID, playerId = A_PLAYER_ID_2),
                                         chosen = false))
     val playersResponse = listOf(PlayerEntity(id = A_PLAYER_ID_1,
                                               atpTourId = AN_ATP_TOUR_ID_1,
                                               fantaPoints = 12.0,
                                               fullName = A_FULL_NAME_1))

    val expected = ErrorTeam

    every { mySqlTeamDao.findByIdTeamId(A_TEAM_ID) } returns teamResponse
    every { playersDao.findAllById(setOf(A_PLAYER_ID_1, A_PLAYER_ID_2)) } returns playersResponse

    assertThat(repository.getTeam(A_TEAM_ID)).isEqualTo(expected)
  }

  companion object {

    private const val A_TEAM_ID = "A_TEAM_ID"
    private const val A_PLAYER_ID_1 = "A_PLAYER_ID_1"
    private const val A_PLAYER_ID_2 = "A_PLAYER_ID_2"
    private const val AN_ATP_TOUR_ID_1 = "AN_ATP_TOUR_ID_1"
    private const val AN_ATP_TOUR_ID_2 = "AN_ATP_TOUR_ID_2"
    private const val A_FULL_NAME_1 = "A_FULL_NAME_1"
    private const val A_FULL_NAME_2 = "A_FULL_NAME_2"
  }
}