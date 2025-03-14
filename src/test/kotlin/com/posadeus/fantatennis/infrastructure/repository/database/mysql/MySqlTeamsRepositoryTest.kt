package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.TeamsRepository
import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.*
import com.posadeus.fantatennis.domain.model.AddPlayers.ValidAddPlayers
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.*
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Optional.empty
import java.util.Optional.of

class MySqlTeamsRepositoryTest {

  private val fantaTeamsDao: FantaTeamsDao = mockk()
  private val tournamentsDao: TournamentsDao = mockk()
  private val playersDao: PlayersDao = mockk()
  private val teamsDao: TeamsDao = mockk()

  private val repository: TeamsRepository = MySqlTeamsRepository(fantaTeamsDao, tournamentsDao, playersDao, teamsDao)

  @Test
  fun `players are all found and added to the team`() {

    val playerIds = setOf("A_PLAYER_ID", "ANOTHER_PLAYER_ID")

    val fantaTeamsEntity = aFantaTeamsEntityWith(1)
    val tournamentsEntity = aTournamentsEntityWith(123)
    val aPlayerEntity = PlayersEntity(id = "A_PLAYER_ID",
                                      atpTourId = AN_ATP_PLAYER_ID,
                                      fullName = A_PLAYER_FULL_NAME)
    val anotherPlayerEntity = PlayersEntity(id = "ANOTHER_PLAYER_ID",
                                            atpTourId = ANOTHER_ATP_PLAYER_ID,
                                            fullName = ANOTHER_PLAYER_FULL_NAME)
    val aTeamEntity = TeamsEntity(id = TeamsKeyEmbedded(teamId = 1, playerId = "A_PLAYER_ID"),
                                  player = aPlayerEntity,
                                  fantaTeam = fantaTeamsEntity,
                                  startingTournament = tournamentsEntity,
                                  endingTournament = null)
    val anotherTeamEntity = TeamsEntity(id = TeamsKeyEmbedded(teamId = 1, playerId = "ANOTHER_PLAYER_ID"),
                                  player = anotherPlayerEntity,
                                  fantaTeam = fantaTeamsEntity,
                                  startingTournament = tournamentsEntity,
                                  endingTournament = null)

    val aDomainPlayer = DomainPlayer(id = "A_PLAYER_ID",
                                     atpId = AN_ATP_PLAYER_ID,
                                     fullName = A_PLAYER_FULL_NAME)
    val anotherDomainPlayer = DomainPlayer(id = "ANOTHER_PLAYER_ID",
                                           atpId = ANOTHER_ATP_PLAYER_ID,
                                           fullName = ANOTHER_PLAYER_FULL_NAME)
    val expected = ValidAddPlayers(players = setOf(aDomainPlayer, anotherDomainPlayer))

    every { fantaTeamsDao.findById(1) } returns of(fantaTeamsEntity)
    every { tournamentsDao.findById(123) } returns of(tournamentsEntity)
    every { playersDao.findAllById(playerIds) } returns listOf(aPlayerEntity, anotherPlayerEntity)
    every { teamsDao.saveAll(setOf(aTeamEntity, anotherTeamEntity)) } returns listOf(aTeamEntity, anotherTeamEntity)

    assertThat(repository.addPlayers(1, playerIds, 123)).isEqualTo(expected)
  }

  @Test
  fun `team not found`() {

    val expected = AddPlayersTeamNotFound

    every { fantaTeamsDao.findById(1) } returns empty()

    assertThat(repository.addPlayers(1, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), A_TOURNAMENT_ID)).isEqualTo(expected)

    verify { tournamentsDao wasNot called }
    verify { playersDao wasNot called }
    verify { teamsDao wasNot called }
  }

  @Test
  fun `tournament not found`() {

    val fantaTeamsEntity = aFantaTeamsEntityWith(1)

    val expected = AddPlayersTournamentNotFound

    every { fantaTeamsDao.findById(1) } returns of(fantaTeamsEntity)
    every { tournamentsDao.findById(123) } returns empty()

    assertThat(repository.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), 123)).isEqualTo(expected)

    verify { playersDao wasNot called }
    verify { teamsDao wasNot called }
  }

  @Test
  fun `one or more players not found`() {

    val playerIds = setOf("A_PLAYER_ID", "ANOTHER_PLAYER_ID")

    val fantaTeamsEntity = aFantaTeamsEntityWith(1)
    val tournamentsEntity = aTournamentsEntityWith(123)
    val aPlayerEntity = PlayersEntity(id = "A_PLAYER_ID",
                                      atpTourId = AN_ATP_PLAYER_ID,
                                      fullName = A_PLAYER_FULL_NAME)

    val expected = PlayersNotFound(missingPlayerIds = setOf("ANOTHER_PLAYER_ID"))

    every { fantaTeamsDao.findById(1) } returns of(fantaTeamsEntity)
    every { tournamentsDao.findById(A_TOURNAMENT_ID) } returns of(tournamentsEntity)
    every { playersDao.findAllById(playerIds) } returns listOf(aPlayerEntity)

    assertThat(repository.addPlayers(A_TEAM_ID, playerIds, A_TOURNAMENT_ID)).isEqualTo(expected)

    verify { teamsDao wasNot called }
  }

  @Test
  fun `exception thrown by any dao`() {

    val expected = AddPlayersError

    every { fantaTeamsDao.findById(A_TEAM_ID) } throws Exception()

    assertThat(repository.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  private fun aTournamentsEntityWith(id: Int) =
      TournamentsEntity(id = id,
                        tennisTvId = A_TOURNAMENT_TENNIS_TV_ID,
                        atpTourId = A_TOURNAMENT_ATP_TOUR_ID,
                        name = A_TOURNAMENT_NAME,
                        points = A_TOURNAMENT_POINTS,
                        location = A_TOURNAMENT_LOCATION,
                        surface = A_TOURNAMENT_SURFACE,
                        year = A_TOURNAMENT_YEAR,
                        startDate = A_START_DATE,
                        endDate = AN_END_DATE)

  private fun aFantaTeamsEntityWith(teamId: Int) =
      FantaTeamsEntity(teamId = teamId,
                       ownerId = AN_OWNER_ID,
                       teams = emptyList())

  companion object {

    private const val A_TEAM_ID = 1
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val AN_ATP_PLAYER_ID = "AN_ATP_PLAYER_ID"
    private const val ANOTHER_ATP_PLAYER_ID = "ANOTHER_ATP_PLAYER_ID"
    private const val A_PLAYER_FULL_NAME = "A_PLAYER_FULL_NAME"
    private const val ANOTHER_PLAYER_FULL_NAME = "ANOTHER_PLAYER_FULL_NAME"
    private const val A_TOURNAMENT_NAME = "A_TOURNAMENT_NAME"
    private const val A_TOURNAMENT_LOCATION = "A_TOURNAMENT_LOCATION"
    private const val A_TOURNAMENT_SURFACE = "A_TOURNAMENT_SURFACE"
    private const val A_START_DATE = "2024-01-01"
    private const val AN_END_DATE = "2024-01-01"
    private const val A_TOURNAMENT_ID = 123
    private const val A_TOURNAMENT_TENNIS_TV_ID = 234
    private const val A_TOURNAMENT_ATP_TOUR_ID = 345
    private const val A_TOURNAMENT_POINTS = 2
    private const val A_TOURNAMENT_YEAR = 2000
  }
}