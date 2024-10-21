package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.TeamsRepository
import com.posadeus.fantatennis.domain.model.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Optional.empty
import java.util.Optional.of

class MySqlTeamsRepositoryTest {

  private val fantaTeamsDao: FantaTeamsDao = mockk()
  private val playersDao: PlayersDao = mockk()
  private val teamsDao: TeamsDao = mockk()

  private val repository: TeamsRepository = MySqlTeamsRepository(fantaTeamsDao, playersDao, teamsDao)

  @Test
  fun `players are all found and added to the team`() {

    val playerIds = setOf("A_PLAYER_ID", "ANOTHER_PLAYER_ID")

    val fantaTeamsEntity = FantaTeamsEntity(teamId = 1,
                                            ownerId = AN_OWNER_ID,
                                            teams = emptyList())
    val aPlayerEntity = PlayersEntity(id = "A_PLAYER_ID",
                                      atpTourId = AN_ATP_PLAYER_ID,
                                      fullName = A_PLAYER_FULL_NAME)
    val anotherPlayerEntity = PlayersEntity(id = "ANOTHER_PLAYER_ID",
                                            atpTourId = ANOTHER_ATP_PLAYER_ID,
                                            fullName = ANOTHER_PLAYER_FULL_NAME)
    val aTeamEntity = TeamsEntity(id = TeamsKeyEmbedded(teamId = 1, playerId = "A_PLAYER_ID"),
                                  player = aPlayerEntity,
                                  fantaTeam = fantaTeamsEntity)
    val anotherTeamEntity = TeamsEntity(id = TeamsKeyEmbedded(teamId = 1, playerId = "ANOTHER_PLAYER_ID"),
                                  player = anotherPlayerEntity,
                                  fantaTeam = fantaTeamsEntity)

    val aDomainPlayer = DomainPlayer(id = "A_PLAYER_ID",
                                     atpId = AN_ATP_PLAYER_ID,
                                     fullName = A_PLAYER_FULL_NAME)
    val anotherDomainPlayer = DomainPlayer(id = "ANOTHER_PLAYER_ID",
                                           atpId = ANOTHER_ATP_PLAYER_ID,
                                           fullName = ANOTHER_PLAYER_FULL_NAME)
    val expected = AddPlayersOk(players = setOf(aDomainPlayer, anotherDomainPlayer))

    every { fantaTeamsDao.findById(1) } returns of(fantaTeamsEntity)
    every { playersDao.findAllById(playerIds) } returns listOf(aPlayerEntity, anotherPlayerEntity)
    every { teamsDao.saveAll(setOf(aTeamEntity, anotherTeamEntity)) } returns listOf(aTeamEntity, anotherTeamEntity)

    assertThat(repository.addPlayers(1, playerIds)).isEqualTo(expected)
  }

  @Test
  fun `team not found`() {

    val expected = AddPlayersTeamNotFound

    every { fantaTeamsDao.findById(1) } returns empty()

    assertThat(repository.addPlayers(1, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID))).isEqualTo(expected)

    verify { playersDao wasNot called }
    verify { teamsDao wasNot called }
  }

  @Test
  fun `one or more players not found`() {

    val playerIds = setOf("A_PLAYER_ID", "ANOTHER_PLAYER_ID")

    val fantaTeamsEntity = FantaTeamsEntity(teamId = 1,
                                            ownerId = AN_OWNER_ID,
                                            teams = emptyList())
    val aPlayerEntity = PlayersEntity(id = "A_PLAYER_ID",
                                      atpTourId = AN_ATP_PLAYER_ID,
                                      fullName = A_PLAYER_FULL_NAME)

    val expected = PlayersNotFound(missingPlayerIds = setOf("ANOTHER_PLAYER_ID"))

    every { fantaTeamsDao.findById(1) } returns of(fantaTeamsEntity)
    every { playersDao.findAllById(playerIds) } returns listOf(aPlayerEntity)

    assertThat(repository.addPlayers(A_TEAM_ID, playerIds)).isEqualTo(expected)

    verify { teamsDao wasNot called }
  }

  companion object {

    private const val A_TEAM_ID = 1
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val AN_ATP_PLAYER_ID = "AN_ATP_PLAYER_ID"
    private const val ANOTHER_ATP_PLAYER_ID = "ANOTHER_ATP_PLAYER_ID"
    private const val A_PLAYER_FULL_NAME = "A_PLAYER_FULL_NAME"
    private const val ANOTHER_PLAYER_FULL_NAME = "ANOTHER_PLAYER_FULL_NAME"
  }
}