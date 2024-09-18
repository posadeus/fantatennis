package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.domain.model.AtpPlayer
import com.posadeus.fantatennis.domain.service.player.FantaPointCalculatorService
import com.posadeus.fantatennis.domain.service.player.FantaPointPersistenceService
import io.mockk.*
import org.junit.jupiter.api.Test

class PlayerFantaPointServiceTest {

  private val fantaPointCalculatorService: FantaPointCalculatorService = mockk()
  private val fantaPointPersistenceService: FantaPointPersistenceService = mockk()

  private val service = PlayerFantaPointService(fantaPointCalculatorService,
                                                fantaPointPersistenceService)

  @Test
  fun `all players found`() {

     val atpPlayers = setOf(AtpPlayer(id = "PlayerId1",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 28.0))),
                            AtpPlayer(id = "PlayerId2",
                                      tournamentPoints = mapOf(A_YEAR to mapOf(A_TOURNAMENT_ID to 16.0))))

    every { fantaPointCalculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } returns atpPlayers
    every { fantaPointPersistenceService.persistScores(atpPlayers) } just runs

    service.playerFantaPointsFor(A_TOURNAMENT_ID, A_YEAR)

    verify(exactly = 1) { fantaPointCalculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) }
    verify(exactly = 1) { fantaPointPersistenceService.persistScores(atpPlayers) }
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1
    private const val A_YEAR = 2000
  }
}