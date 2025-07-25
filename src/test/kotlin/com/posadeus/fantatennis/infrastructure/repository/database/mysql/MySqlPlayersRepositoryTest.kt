package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.PlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayersEntity
import io.mockk.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MySqlPlayersRepositoryTest {

  private val playersDao: PlayersDao = mockk()

  private val repository: PlayersRepository = MySqlPlayersRepository(playersDao)

  @Nested
  inner class SavePlayers {

    @Test
    fun `save all players`() {

      val domainPlayer1 = DomainPlayer(id = AN_ID,
                                       atpId = AN_ATP_ID,
                                       fullName = A_FULL_NAME)
      val domainPlayer2 = DomainPlayer(id = ANOTHER_ID,
                                       atpId = ANOTHER_ATP_ID,
                                       fullName = ANOTHER_FULL_NAME)
      val players = setOf(domainPlayer1, domainPlayer2)

      val playersEntity1 = PlayersEntity(id = AN_ID,
                                         atpTourId = AN_ATP_ID,
                                         fullName = A_FULL_NAME)

      val playersEntity2 = PlayersEntity(id = ANOTHER_ID,
                                         atpTourId = ANOTHER_ATP_ID,
                                         fullName = ANOTHER_FULL_NAME)
      val playersEntities = setOf(playersEntity1, playersEntity2)

      every { playersDao.saveAll(playersEntities) } returns playersEntities

      repository.saveAll(players)

      verify(exactly = 1) { playersDao.saveAll(playersEntities) }
    }
  }

  companion object {

    private const val AN_ID = "AN_ID"
    private const val ANOTHER_ID = "ANOTHER_ID"
    private const val AN_ATP_ID = "AN_ATP_ID"
    private const val ANOTHER_ATP_ID = "ANOTHER_ATP_ID"
    private const val A_FULL_NAME = "A_FULL_NAME"
    private const val ANOTHER_FULL_NAME = "ANOTHER_FULL_NAME"
  }
}