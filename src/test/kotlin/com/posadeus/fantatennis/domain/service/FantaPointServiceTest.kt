package com.posadeus.fantatennis.domain.service

import com.posadeus.fantatennis.domain.model.FailureReason.NO_POINTS_FOR_TOURNAMENT
import com.posadeus.fantatennis.domain.model.FailureReason.PERSISTENCE_ERROR
import com.posadeus.fantatennis.domain.model.FantaPointPersistence.FantaPointPersistenceFailure
import com.posadeus.fantatennis.domain.model.TestAtpPlayer.anAtpPlayer
import com.posadeus.fantatennis.domain.service.player.*
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class FantaPointServiceTest {

  private val fantaPointCalculatorService: FantaPointCalculatorService = mockk()
  private val fantaPointPersistenceService: FantaPointPersistenceService = mockk()

  private val service = FantaPointService(fantaPointCalculatorService,
                                          fantaPointPersistenceService)

  @Test
  fun `tournament players not found`() {

    val expected = FantaPointPersistenceFailure(reason = NO_POINTS_FOR_TOURNAMENT)

    every { fantaPointCalculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } returns emptySet()

    assertThat(service.updateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  @Test
  fun `tournament player persistence fails`() {

    val atpPlayers = setOf(anAtpPlayer(id = AN_ATP_PLAYER_ID),
                           anAtpPlayer(id = ANOTHER_ATP_PLAYER_ID))

    val expected = FantaPointPersistenceFailure(reason = PERSISTENCE_ERROR)

    every { fantaPointCalculatorService.calculateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR) } returns atpPlayers
    every { fantaPointPersistenceService.persist(atpPlayers) } returns expected

    assertThat(service.updateFantaPointsFor(A_TOURNAMENT_ID, A_YEAR)).isEqualTo(expected)
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1
    private const val A_YEAR = 2000
    private const val AN_ATP_PLAYER_ID = "AN_ATP_PLAYER_ID"
    private const val ANOTHER_ATP_PLAYER_ID = "ANOTHER_ATP_PLAYER_ID"
  }
}