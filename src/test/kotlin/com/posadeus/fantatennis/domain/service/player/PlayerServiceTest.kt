package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceSucceeded
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlayerServiceTest {

  private val retrievePlayersRepository: RetrievePlayersRepository = mockk()
  private val persistPlayersRepository: PersistPlayersRepository = mockk()

  private val service = PlayerService(retrievePlayersRepository, persistPlayersRepository)

  @Test
  fun `get all players`() {

    val domainPlayer1 = DomainPlayer(id = AN_ID,
                                     atpId = AN_ATP_ID,
                                     fullName = A_FULL_NAME)
    val domainPlayer2 = DomainPlayer(id = ANOTHER_ID,
                                     atpId = ANOTHER_ATP_ID,
                                     fullName = ANOTHER_FULL_NAME)
    val expected = setOf(domainPlayer1, domainPlayer2)

    every { retrievePlayersRepository.retrieve() } returns expected

    assertThat(service.allPlayers()).isEqualTo(expected)
  }

  @Test
  fun `persist players`() {

    val domainPlayers = setOf(DomainPlayer(id = AN_ID,
                                           atpId = AN_ATP_ID,
                                           fullName = A_FULL_NAME))

    every { persistPlayersRepository.persistAll(domainPlayers) } returns PlayerPersistenceSucceeded

    service.saveAll(domainPlayers)

    verify(exactly = 1) { persistPlayersRepository.persistAll(domainPlayers) }
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