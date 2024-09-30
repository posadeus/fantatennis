package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.domain.infrastructure.PlayersRepository
import com.posadeus.fantatennis.domain.model.AtpPlayer
import io.mockk.*
import org.junit.jupiter.api.Test

class FantaPointPersistenceServiceTest {

  private val playerPointsRepository: PlayerPointsRepository = mockk()
  private val playersRepository: PlayersRepository = mockk()

  private val service = FantaPointPersistenceService(playerPointsRepository)

  @Test
  fun `persist scores`() {

    val players = setOf(AtpPlayer(id = "PlayerId1",
                                  tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 28.0))),
                        AtpPlayer(id = "PlayerId2",
                                  tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 16.0))))

    every { playerPointsRepository.save(players) } just runs

    service.persistScores(players)

    verify(exactly = 1) { playerPointsRepository.save(players) }
    verify { playersRepository wasNot called }
  }

  @Test
  fun `repository not called if players is empty`() {

    val players = emptySet<AtpPlayer>()

    service.persistScores(players)

    verify { playerPointsRepository wasNot called }
    verify { playersRepository wasNot called }
  }

  companion object {

    private const val A_TOURNAMENT_ID = 123
    private const val A_YEAR = 2222
  }
}