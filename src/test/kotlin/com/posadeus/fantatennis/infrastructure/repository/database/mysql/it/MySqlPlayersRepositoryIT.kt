package com.posadeus.fantatennis.infrastructure.repository.database.mysql.it

import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlPlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayersEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
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
class MySqlPlayersRepositoryIT {

  @Autowired
  private lateinit var playersDao: PlayersDao

  @Autowired
  private lateinit var mySqlPlayersRepository: MySqlPlayersRepository

  @BeforeEach
  fun setUp() {

    deleteAll()
  }

  @Nested
  inner class RetrievePlayers {

    @Test
    fun `retrieve saved players`() {

      assertThat(playersDao.findAll()).isEqualTo(arrayListOf<PlayersEntity>())

      val player1 = PlayersEntity(id = "AN_ID",
                                  atpTourId = "AN_ATP_TOUR_ID",
                                  fullName = "A_FULL_NAME")
      val player2 = PlayersEntity(id = "ANOTHER_ID",
                                  atpTourId = "ANOTHER_ATP_TOUR_ID",
                                  fullName = "ANOTHER_FULL_NAME")

      playersDao.saveAll(listOf(player1, player2))

      val domainPlayer1 = DomainPlayer(id = "AN_ID",
                                       atpId = "AN_ATP_TOUR_ID",
                                       fullName = "A_FULL_NAME")
      val domainPlayer2 = DomainPlayer(id = "ANOTHER_ID",
                                       atpId = "ANOTHER_ATP_TOUR_ID",
                                       fullName = "ANOTHER_FULL_NAME")
      val expected = setOf(domainPlayer1, domainPlayer2)

      assertThat(mySqlPlayersRepository.getAllPlayers()).isEqualTo(expected)
    }
  }

  @Nested
  inner class SavePlayers {

    @Test
    fun `save players`() {

      assertThat(playersDao.findAll()).isEqualTo(arrayListOf<PlayersEntity>())

      val domainPlayer1 = DomainPlayer(id = "AN_ID",
                                       atpId = "AN_ATP_TOUR_ID",
                                       fullName = "A_FULL_NAME")
      val domainPlayer2 = DomainPlayer(id = "ANOTHER_ID",
                                       atpId = "ANOTHER_ATP_TOUR_ID",
                                       fullName = "ANOTHER_FULL_NAME")
      val players = setOf(domainPlayer1, domainPlayer2)

      val player1 = PlayersEntity(id = "AN_ID",
                                  atpTourId = "AN_ATP_TOUR_ID",
                                  fullName = "A_FULL_NAME")
      val player2 = PlayersEntity(id = "ANOTHER_ID",
                                  atpTourId = "ANOTHER_ATP_TOUR_ID",
                                  fullName = "ANOTHER_FULL_NAME")
      val expected = arrayListOf(player1, player2)

      mySqlPlayersRepository.saveAll(players)

      assertThat(playersDao.findAll()).usingRecursiveComparison().isEqualTo(expected)
    }
  }

  private fun deleteAll() {

    playersDao.deleteAll()
  }
}