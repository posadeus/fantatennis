package com.posadeus.fantatennis.domain.service.player

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceFailure
import com.posadeus.fantatennis.domain.model.PlayerPersistence.PlayerPersistenceSuccess
import com.posadeus.fantatennis.domain.model.TestDomainPlayer.aDomainPlayer
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class PersistRetrievePlayerServiceTest {

  private val persistPlayersRepository: PersistPlayersRepository = mockk()

  private val service = PersistPlayerService(persistPlayersRepository)

  @Test
  fun `player persistence fails`() {

    val players = setOf(aDomainPlayer(), aDomainPlayer())

    val expected = PlayerPersistenceFailure(A_MESSAGE, AN_ERROR)

    every { persistPlayersRepository.persistAll(players) } returns expected

    assertThat(service.persistAll(players)).isEqualTo(expected)
  }

  @Test
  fun `player persistence succeeds`() {

    val players = setOf(aDomainPlayer(), aDomainPlayer())

    val expected = PlayerPersistenceSuccess

    every { persistPlayersRepository.persistAll(players) } returns expected

    assertThat(service.persistAll(players)).isEqualTo(expected)
  }

  companion object {

    private const val A_MESSAGE = "A_MESSAGE"
    private const val AN_ERROR = "AN_ERROR"
  }
}