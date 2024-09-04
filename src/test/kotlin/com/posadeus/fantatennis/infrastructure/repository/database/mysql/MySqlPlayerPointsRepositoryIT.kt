package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayersPointsEntity
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayersPointsKeyEmbedded
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
  private lateinit var mySqlPlayerPointsRepository: MySqlPlayerPointsRepository

  @BeforeEach
  fun setUp() {

    deleteAll()
  }

  @Test
  fun `players saved`() {

    assertThat(playersPointsDao.findAll()).isEqualTo(arrayListOf<PlayersPointsEntity>())

    val players = setOf(DomainPlayer(id = "AN_ID", tournamentPoints = mapOf(2222 to mapOf(1234 to 10.0))),
                        DomainPlayer(id = "ANOTHER_ID", tournamentPoints = mapOf(2222 to mapOf(1234 to 13.0))))

    val entity1 = PlayersPointsEntity(PlayersPointsKeyEmbedded(2222, 1234, "AN_ID"), 10.0)
    val entity2 = PlayersPointsEntity(PlayersPointsKeyEmbedded(2222, 1234, "ANOTHER_ID"), 13.0)
    val expected = arrayListOf(entity1, entity2)

    mySqlPlayerPointsRepository.save(players)

    assertThat(playersPointsDao.findAll()).isEqualTo(expected)
  }

  @Test
  fun `players already present are overridden`() {

    assertThat(playersPointsDao.findAll()).isEqualTo(arrayListOf<PlayersPointsEntity>())

    val entity1 = PlayersPointsEntity(PlayersPointsKeyEmbedded(2222, 1234, "AN_ID"), 10.0)
    val entity2 = PlayersPointsEntity(PlayersPointsKeyEmbedded(2222, 1234, "ANOTHER_ID"), 13.0)
    val playersPointsEntities = arrayListOf(entity1, entity2)

    playersPointsDao.saveAll(playersPointsEntities)

    assertThat(playersPointsDao.findAll()).isEqualTo(playersPointsEntities)

    val players = setOf(DomainPlayer(id = "AN_ID", tournamentPoints = mapOf(2222 to mapOf(1234 to 15.0))),
                        DomainPlayer(id = "ANOTHER_ID", tournamentPoints = mapOf(2222 to mapOf(1234 to 22.22))))

    mySqlPlayerPointsRepository.save(players)

    val entityUpdated1 = PlayersPointsEntity(PlayersPointsKeyEmbedded(2222, 1234, "AN_ID"), 15.0)
    val entityUpdated2 = PlayersPointsEntity(PlayersPointsKeyEmbedded(2222, 1234, "ANOTHER_ID"), 22.22)
    val expected = arrayListOf(entityUpdated1, entityUpdated2)

    assertThat(playersPointsDao.findAll()).isEqualTo(expected)
  }

  private fun deleteAll() {

    playersPointsDao.deleteAll()
  }
}