package com.posadeus.fantatennis.infrastructure.repository.database.mysql

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.model.AtpPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.model.*
import io.mockk.*
import org.junit.jupiter.api.Test

class MySqlPlayerPointsRepositoryTest {

  private val playersPointsDao: PlayersPointsDao = mockk()

  private val repository: PlayerPointsRepository = MySqlPlayerPointsRepository(playersPointsDao)

  @Test
  fun `save players`() {

    val players = setOf(AtpPlayer(id = "AN_ID", tournamentPoints = mapOf(2222 to mapOf(1234 to 10.0))),
                        AtpPlayer(id = "ANOTHER_ID", tournamentPoints = mapOf(2222 to mapOf(1234 to 13.0))))

    val playersPointsKeyEmbedded1 = PlayersPointsKeyEmbedded(2222, 1234, "AN_ID")
    val playersPointsKeyEmbedded2 = PlayersPointsKeyEmbedded(2222, 1234, "ANOTHER_ID")
    val player1 = PlayersEntity(id = "AN_ID")
    val player2 = PlayersEntity(id = "ANOTHER_ID")
    val tournament = TournamentsEntity(1234)
    val entity1 = PlayersPointsEntity(playersPointsKeyEmbedded1, 10.0, player1, tournament)
    val entity2 = PlayersPointsEntity(playersPointsKeyEmbedded2, 13.0, player2, tournament)
    val playersPointsEntities = setOf(entity1, entity2)

    every { playersPointsDao.saveAll(playersPointsEntities) } returns playersPointsEntities

    repository.save(players)

    verify { playersPointsDao.saveAll(playersPointsEntities) }
  }
}