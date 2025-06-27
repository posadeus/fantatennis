package com.posadeus.fantatennis.infrastructure.repository.database.mysql.it

import com.posadeus.fantatennis.domain.model.AddPlayers.InvalidAddPlayers.*
import com.posadeus.fantatennis.domain.model.AddPlayers.ValidAddPlayers
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlTeamsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.*
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
class MySqlTeamsRepositoryIT {

  @Autowired
  private lateinit var fantaTeamsDao: FantaTeamsDao

  @Autowired
  private lateinit var playersDao: PlayersDao

  @Autowired
  private lateinit var teamsDao: TeamsDao

  @Autowired
  private lateinit var tournamentsDao: TournamentsDao

  @Autowired
  private lateinit var mySqlTeamsRepository: MySqlTeamsRepository

  @BeforeEach
  fun setUp() {

    deleteAll()
  }

  @Test
  fun `team saved`() {

    assertThat(fantaTeamsDao.findAll()).isEqualTo(arrayListOf<FantaTeamsEntity>())
    assertThat(tournamentsDao.findAll()).isEqualTo(arrayListOf<TournamentsEntity>())
    assertThat(playersDao.findAll()).isEqualTo(arrayListOf<PlayersEntity>())
    assertThat(teamsDao.findAll()).isEqualTo(arrayListOf<TeamsEntity>())

    val fantaTeamEntity = FantaTeamsEntity(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)
    fantaTeamsDao.save(fantaTeamEntity)

    val tournamentEntity = TournamentsEntity(id = A_TOURNAMENT_ID,
                                             tennisTvId = A_TOURNAMENT_TENNIS_TV_ID,
                                             atpTourId = A_TOURNAMENT_ATP_TOUR_ID,
                                             name = A_TOURNAMENT_NAME,
                                             points = A_TOURNAMENT_POINTS,
                                             location = A_TOURNAMENT_LOCATION,
                                             surface = A_TOURNAMENT_SURFACE,
                                             year = A_TOURNAMENT_YEAR)

    tournamentsDao.save(tournamentEntity)

    val aPlayerEntity = PlayersEntity(id = A_PLAYER_ID,
                                      atpTourId = AN_ATP_TOUR_ID,
                                      fullName = A_FULL_NAME)
    val anotherPlayerEntity = PlayersEntity(id = ANOTHER_PLAYER_ID,
                                            atpTourId = ANOTHER_ATP_TOUR_ID,
                                            fullName = ANOTHER_FULL_NAME)
    playersDao.saveAll(listOf(aPlayerEntity, anotherPlayerEntity))

    val aDomainPlayer = DomainPlayer(id = A_PLAYER_ID,
                                     atpId = AN_ATP_TOUR_ID,
                                     fullName = A_FULL_NAME)

    val anotherDomainPlayer = DomainPlayer(id = ANOTHER_PLAYER_ID,
                                           atpId = ANOTHER_ATP_TOUR_ID,
                                           fullName = ANOTHER_FULL_NAME)
    val expected = ValidAddPlayers(players = setOf(aDomainPlayer, anotherDomainPlayer))

    assertThat(mySqlTeamsRepository.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `team not found`() {

    assertThat(fantaTeamsDao.findAll()).isEqualTo(arrayListOf<FantaTeamsEntity>())

    val expected = AddPlayersTeamNotFound

    assertThat(mySqlTeamsRepository.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `tournament not found`() {

    assertThat(fantaTeamsDao.findAll()).isEqualTo(arrayListOf<FantaTeamsEntity>())
    assertThat(tournamentsDao.findAll()).isEqualTo(arrayListOf<TournamentsEntity>())

    val fantaTeamEntity = FantaTeamsEntity(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)
    fantaTeamsDao.save(fantaTeamEntity)

    val expected = AddPlayersTournamentNotFound

    assertThat(mySqlTeamsRepository.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  @Test
  fun `one or more players not found`() {

    assertThat(fantaTeamsDao.findAll()).isEqualTo(arrayListOf<FantaTeamsEntity>())
    assertThat(tournamentsDao.findAll()).isEqualTo(arrayListOf<TournamentsEntity>())
    assertThat(playersDao.findAll()).isEqualTo(arrayListOf<PlayersEntity>())

    val fantaTeamEntity = FantaTeamsEntity(teamId = A_TEAM_ID, ownerId = AN_OWNER_ID)
    fantaTeamsDao.save(fantaTeamEntity)

    val tournamentEntity = TournamentsEntity(id = A_TOURNAMENT_ID,
                                             tennisTvId = A_TOURNAMENT_TENNIS_TV_ID,
                                             atpTourId = A_TOURNAMENT_ATP_TOUR_ID,
                                             name = A_TOURNAMENT_NAME,
                                             points = A_TOURNAMENT_POINTS,
                                             location = A_TOURNAMENT_LOCATION,
                                             surface = A_TOURNAMENT_SURFACE,
                                             year = A_TOURNAMENT_YEAR)

    tournamentsDao.save(tournamentEntity)

    val aPlayerEntity = PlayersEntity(id = A_PLAYER_ID,
                                      atpTourId = AN_ATP_TOUR_ID,
                                      fullName = A_FULL_NAME)
    playersDao.saveAll(listOf(aPlayerEntity))

    val expected = PlayersNotFound(missingPlayerIds = setOf(ANOTHER_PLAYER_ID))

    assertThat(mySqlTeamsRepository.addPlayers(A_TEAM_ID, setOf(A_PLAYER_ID, ANOTHER_PLAYER_ID), A_TOURNAMENT_ID)).isEqualTo(expected)
  }

  private fun deleteAll() {

    fantaTeamsDao.deleteAll()
    tournamentsDao.deleteAll()
    playersDao.deleteAll()
    teamsDao.deleteAll()
  }

  companion object {

    private const val A_TEAM_ID = 1
    private const val A_PLAYER_ID = "A_PLAYER_ID"
    private const val ANOTHER_PLAYER_ID = "ANOTHER_PLAYER_ID"
    private const val AN_ATP_TOUR_ID = "AN_ATP_TOUR_ID"
    private const val ANOTHER_ATP_TOUR_ID = "ANOTHER_ATP_TOUR_ID"
    private const val A_FULL_NAME = "A_FULL_NAME"
    private const val ANOTHER_FULL_NAME = "ANOTHER_FULL_NAME"
    private const val AN_OWNER_ID = "AN_OWNER_ID"
    private const val A_TOURNAMENT_NAME = "A_TOURNAMENT_NAME"
    private const val A_TOURNAMENT_LOCATION = "A_TOURNAMENT_LOCATION"
    private const val A_TOURNAMENT_SURFACE = "A_TOURNAMENT_SURFACE"
    private const val A_TOURNAMENT_ID = 1
    private const val A_TOURNAMENT_TENNIS_TV_ID = 456
    private const val A_TOURNAMENT_ATP_TOUR_ID = 789
    private const val A_TOURNAMENT_POINTS = 2
    private const val A_TOURNAMENT_YEAR = 2000
  }
}