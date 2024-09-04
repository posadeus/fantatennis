package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.controller.model.team.TeamDto
import com.posadeus.fantatennis.controller.model.team.TeamPlayerDto
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TeamsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DataJpaTest
@ContextConfiguration(classes = [com.posadeus.fantatennis.app.Application::class])
@ComponentScan(basePackages = ["com.posadeus.fantatennis.app.configuration.infrastructure.mysql"])
class MySqlTeamRepositoryIT {

  @Autowired
  private lateinit var teamDao: TeamsDao
  @Autowired
  private lateinit var playerDao: PlayersDao

  @Autowired
  private lateinit var mySqlTeamRepository: MySqlTeamRepository

  @BeforeEach
  fun setUp() {

    deleteAll()
  }

  @Test
  fun `team retrieved from DB`() {

    val teamRecords = listOf(TeamsEntity(id = TeamsKeyEmbedded(teamId = "A_TEAM_ID", playerId = "A_PLAYER_ID_1"),
                                         chosen = true),
                             TeamsEntity(id = TeamsKeyEmbedded(teamId = "A_TEAM_ID", playerId = "A_PLAYER_ID_2"),
                                         chosen = false))
    val playerRecords = listOf(PlayersEntity(id = "A_PLAYER_ID_1",
                                             atpTourId = "AN_ATP_TOUR_ID_1",
                                             fantaPoints = 12.0,
                                             fullName = "A_FULL_NAME_1"),
                               PlayersEntity(id = "A_PLAYER_ID_2",
                                             atpTourId = "AN_ATP_TOUR_ID_2",
                                             fantaPoints = 22.2,
                                             fullName = "A_FULL_NAME_2"))
    teamDao.saveAll(teamRecords)
    playerDao.saveAll(playerRecords)

    val player1 = TeamPlayerDto(fullName = "A_FULL_NAME_1",
                                fantaPoints = 12.0,
                                chosen = true)
    val player2 = TeamPlayerDto(fullName = "A_FULL_NAME_2",
                                fantaPoints = 22.2,
                                chosen = false)
    val expected = FoundTeam(team = TeamDto(players = listOf(player1, player2),
                                            totalScore = 34.2,
                                            completed = false))

    assertThat(mySqlTeamRepository.getTeam("A_TEAM_ID")).isEqualTo(expected)
  }

  @Test
  fun `teamDao is empty`() {

    val expected = EmptyTeam

    assertThat(mySqlTeamRepository.getTeam("A_TEAM_ID")).isEqualTo(expected)
  }

  @Test
  fun `playersDao is empty`() {

    val teamRecords = listOf(TeamsEntity(id = TeamsKeyEmbedded(teamId = "A_TEAM_ID", playerId = "A_PLAYER_ID_1"),
                                         chosen = true),
                             TeamsEntity(id = TeamsKeyEmbedded(teamId = "A_TEAM_ID", playerId = "A_PLAYER_ID_2"),
                                         chosen = false))

    teamDao.saveAll(teamRecords)

    val expected = ErrorTeam

    assertThat(mySqlTeamRepository.getTeam("A_TEAM_ID")).isEqualTo(expected)
  }

  @Test
  fun `playersDao doesn't contains all the players`() {

    val teamRecords = listOf(TeamsEntity(id = TeamsKeyEmbedded(teamId = "A_TEAM_ID", playerId = "A_PLAYER_ID_1"),
                                         chosen = true),
                             TeamsEntity(id = TeamsKeyEmbedded(teamId = "A_TEAM_ID", playerId = "A_PLAYER_ID_2"),
                                         chosen = false))
     val playerRecord = PlayersEntity(id = "A_PLAYER_ID_1",
                                      atpTourId = "AN_ATP_TOUR_ID_1",
                                      fantaPoints = 12.0,
                                      fullName = "A_FULL_NAME_1")

    teamDao.saveAll(teamRecords)
    playerDao.save(playerRecord)

    val expected = ErrorTeam

    assertThat(mySqlTeamRepository.getTeam("A_TEAM_ID")).isEqualTo(expected)
  }

  private fun deleteAll() {

    teamDao.deleteAll()
    playerDao.deleteAll()
  }
}