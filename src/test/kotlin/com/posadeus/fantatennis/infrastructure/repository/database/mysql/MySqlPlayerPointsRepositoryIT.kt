package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.model.*
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
class MySqlPlayerPointsRepositoryIT {

  @Autowired
  private lateinit var playersPointsDao: PlayersPointsDao

  @Autowired
  private lateinit var playersDao: PlayersDao

  @Autowired
  private lateinit var tournamentsDao: TournamentsDao

  @Autowired
  private lateinit var fantaTeamsDao: FantaTeamsDao

  @Autowired
  private lateinit var teamsDao: TeamsDao

  @Autowired
  private lateinit var mySqlPlayerPointsRepository: MySqlPlayerPointsRepository

  @BeforeEach
  fun setUp() {

    deleteAll()
  }

  @Test
  fun `players saved`() {

    assertThat(playersPointsDao.findAll()).isEqualTo(arrayListOf<PlayersPointsEntity>())

    val player1 = PlayersEntity("AN_ID")
    val player2 = PlayersEntity("ANOTHER_ID")

    playersDao.saveAll(listOf(player1, player2))

    val tournament = TournamentsEntity(1234)

    tournamentsDao.save(tournament)

    val players = setOf(DomainPlayer(id = "AN_ID", tournamentPoints = mapOf(2222 to mapOf(1234 to 10.0))),
                        DomainPlayer(id = "ANOTHER_ID", tournamentPoints = mapOf(2222 to mapOf(1234 to 13.0))))

    val playersPointsKeyEmbedded1 = PlayersPointsKeyEmbedded(2222, 1234, "AN_ID")
    val playersPointsKeyEmbedded2 = PlayersPointsKeyEmbedded(2222, 1234, "ANOTHER_ID")
    val entity1 = PlayersPointsEntity(playersPointsKeyEmbedded1, 10.0, player1, tournament)
    val entity2 = PlayersPointsEntity(playersPointsKeyEmbedded2, 13.0, player2, tournament)

    mySqlPlayerPointsRepository.save(players)

    val result = playersPointsDao.findAll().toList()

    assertThat(result[0].id).isEqualTo(entity1.id)
    assertThat(result[0].fantaPoints).isEqualTo(entity1.fantaPoints)
    assertThat(result[0].tournament.id).isEqualTo(entity1.tournament.id)
    assertThat(result[0].player.id).isEqualTo(entity1.player.id)

    assertThat(result[1].id).isEqualTo(entity2.id)
    assertThat(result[1].fantaPoints).isEqualTo(entity2.fantaPoints)
    assertThat(result[1].tournament.id).isEqualTo(entity2.tournament.id)
    assertThat(result[1].player.id).isEqualTo(entity2.player.id)
  }

  @Test
  fun `players already present are overridden`() {

    assertThat(playersPointsDao.findAll()).isEqualTo(arrayListOf<PlayersPointsEntity>())

    val player1 = PlayersEntity("AN_ID")
    val player2 = PlayersEntity("ANOTHER_ID")

    playersDao.saveAll(listOf(player1, player2))

    val tournament = TournamentsEntity(1234)

    tournamentsDao.save(tournament)

    val playersPointsKeyEmbedded1 = PlayersPointsKeyEmbedded(2222, 1234, "AN_ID")
    val playersPointsKeyEmbedded2 = PlayersPointsKeyEmbedded(2222, 1234, "ANOTHER_ID")
    val entity1 = PlayersPointsEntity(playersPointsKeyEmbedded1, 10.0, player1, tournament)
    val entity2 = PlayersPointsEntity(playersPointsKeyEmbedded2, 13.0, player2, tournament)
    val playersPointsEntities = listOf(entity1, entity2)

    playersPointsDao.saveAll(playersPointsEntities)

    val result = playersPointsDao.findAll().toList()

    assertThat(result[0].id).isEqualTo(entity1.id)
    assertThat(result[0].fantaPoints).isEqualTo(entity1.fantaPoints)
    assertThat(result[0].tournament.id).isEqualTo(entity1.tournament.id)
    assertThat(result[0].player.id).isEqualTo(entity1.player.id)

    assertThat(result[1].id).isEqualTo(entity2.id)
    assertThat(result[1].fantaPoints).isEqualTo(entity2.fantaPoints)
    assertThat(result[1].tournament.id).isEqualTo(entity2.tournament.id)
    assertThat(result[1].player.id).isEqualTo(entity2.player.id)

    val players = setOf(DomainPlayer(id = "AN_ID", tournamentPoints = mapOf(2222 to mapOf(1234 to 15.0))),
                        DomainPlayer(id = "ANOTHER_ID", tournamentPoints = mapOf(2222 to mapOf(1234 to 22.22))))

    mySqlPlayerPointsRepository.save(players)

    val entityUpdated1 = PlayersPointsEntity(playersPointsKeyEmbedded1, 15.0, player1, tournament)
    val entityUpdated2 = PlayersPointsEntity(playersPointsKeyEmbedded2, 22.22, player2, tournament)

    val updateResult = playersPointsDao.findAll().toList()

    assertThat(updateResult[0].fantaPoints).isEqualTo(entityUpdated1.fantaPoints)
    assertThat(updateResult[1].fantaPoints).isEqualTo(entityUpdated2.fantaPoints)
  }

  @Test
  fun `retrieve totalPoints for each player of a team`() {

    assertThat(playersPointsDao.findAll()).isEqualTo(arrayListOf<PlayersPointsEntity>())

    val player1 = PlayersEntity(id = "AN_ID", fullName = "A_PLAYER_NAME_1")
    val player2 = PlayersEntity(id = "ANOTHER_ID", fullName = "A_PLAYER_NAME_2")

    playersDao.saveAll(listOf(player1, player2))

    val tournament = TournamentsEntity(1234)

    tournamentsDao.save(tournament)

    val playersPointsKeyEmbedded1 = PlayersPointsKeyEmbedded(2222, 1234, "AN_ID")
    val playersPointsKeyEmbedded2 = PlayersPointsKeyEmbedded(2222, 1234, "ANOTHER_ID")
    val entity1 = PlayersPointsEntity(playersPointsKeyEmbedded1, 10.0, player1, tournament)
    val entity2 = PlayersPointsEntity(playersPointsKeyEmbedded2, 13.0, player2, tournament)
    val playersPointsEntities = listOf(entity1, entity2)

    playersPointsDao.saveAll(playersPointsEntities)

    val fantaTeam = FantaTeamsEntity(teamId = 1)

    fantaTeamsDao.save(fantaTeam)

    val team1 = TeamsEntity(id = TeamsKeyEmbedded(teamId = 1, playerId = "AN_ID"), player = player1, fantaTeam = fantaTeam)
    val team2 = TeamsEntity(id = TeamsKeyEmbedded(teamId = 1, playerId = "ANOTHER_ID"), player = player2, fantaTeam = fantaTeam)

    teamsDao.saveAll(listOf(team1, team2))

    val tournamentByTeam = FoundTournamentByTeam(teamId = 1,
                                                 startingTournamentId = 1234,
                                                 endingTournamentId = 1234,
                                                 tournamentYear = 2222)

    val playerPoints1 = PlayerPoints(playerId = "ANOTHER_ID", playerName = "A_PLAYER_NAME_2", totalPoints = 13.0)
    val playerPoints2 = PlayerPoints(playerId = "AN_ID", playerName = "A_PLAYER_NAME_1", totalPoints = 10.0)
    val expected = TeamOrderedPlayerPoints(listOf(playerPoints1, playerPoints2))

    assertThat(mySqlPlayerPointsRepository.retrieve(tournamentByTeam)).isEqualTo(expected)
  }

  private fun deleteAll() {

    playersPointsDao.deleteAll()
    playersDao.deleteAll()
    tournamentsDao.deleteAll()
    teamsDao.deleteAll()
    fantaTeamsDao.deleteAll()
  }
}