package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayersPointsEmbedded
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.PlayersPointsEntity
import io.mockk.*
import org.junit.jupiter.api.Test

class MySqlPlayerPointsRepositoryConfigurationTest {

  private val playersPointsDao: PlayersPointsDao = mockk()

  private val repository: PlayerPointsRepository = MySqlPlayerPointsRepository(playersPointsDao)

  @Test
  fun `save players`() {

    val players = setOf(DomainPlayer(id = "AN_ID", tournamentPoints = mapOf(2222 to mapOf(1234 to 10.0))),
                        DomainPlayer(id = "ANOTHER_ID", tournamentPoints = mapOf(2222 to mapOf(1234 to 13.0))))

    val entity1 = PlayersPointsEntity(PlayersPointsEmbedded(2222, "1234", "AN_ID"), 10.0)
    val entity2 = PlayersPointsEntity(PlayersPointsEmbedded(2222, "1234", "ANOTHER_ID"), 13.0)
    val playersPointsEntities = setOf(entity1, entity2)

    every { playersPointsDao.saveAll(playersPointsEntities) } returns playersPointsEntities

    repository.save(players)

    verify { playersPointsDao.saveAll(playersPointsEntities) }
  }
}