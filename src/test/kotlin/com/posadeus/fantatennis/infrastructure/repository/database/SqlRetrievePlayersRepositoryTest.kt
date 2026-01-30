package com.posadeus.fantatennis.infrastructure.repository.database

import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.domain.model.DomainPlayer
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class SqlRetrievePlayersRepositoryTest {

  private val cachedPlayerDao: PlayerDao = mockk()

  private val repository: RetrievePlayersRepository = SqlRetrievePlayersRepository(cachedPlayerDao)

  @Test
  fun `retrieve fails due to exception`() {

    val expected = emptySet<DomainPlayer>()

    every { cachedPlayerDao.retrieveAll() } throws RuntimeException()

    assertThat(repository.retrieve()).isEqualTo(expected)
  }

  @Test
  fun `no players found`() {

    val expected = emptySet<DomainPlayer>()

    every { cachedPlayerDao.retrieveAll() } returns emptyList()

    assertThat(repository.retrieve()).isEqualTo(expected)
  }

  @Test
  fun `retrieve players successful`() {

    val playerDto1 = JdbcPlayerDto(playerId = AN_ID, atpTourId = AN_ATP_ID, fullName = A_FULL_NAME)
    val playerDto2 = JdbcPlayerDto(playerId = ANOTHER_ID, atpTourId = ANOTHER_ATP_ID, fullName = ANOTHER_FULL_NAME)
    val playerDto3 = JdbcPlayerDto(playerId = A_THIRD_ID, atpTourId = A_THIRD_ATP_ID, fullName = A_THIRD_FULL_NAME, rolandGarrosId = A_RG_ID)
    val jdbcPlayers = listOf(playerDto1, playerDto2, playerDto3)

    val player1 = DomainPlayer(id = AN_ID, atpId = AN_ATP_ID, fullName = A_FULL_NAME)
    val player2 = DomainPlayer(id = ANOTHER_ID, atpId = ANOTHER_ATP_ID, fullName = ANOTHER_FULL_NAME)
    val player3 = DomainPlayer(id = A_THIRD_ID, atpId = A_THIRD_ATP_ID, fullName = A_THIRD_FULL_NAME, rolandGarrosId = A_RG_ID)
    val expected = setOf(player1, player2, player3)

    every { cachedPlayerDao.retrieveAll() } returns jdbcPlayers

    assertThat(repository.retrieve()).isEqualTo(expected)
  }

  companion object {

    private const val AN_ID = "AN_ID"
    private const val ANOTHER_ID = "ANOTHER_ID"
    private const val A_THIRD_ID = "A_THIRD_ID"
    private const val AN_ATP_ID = "AN_ATP_ID"
    private const val ANOTHER_ATP_ID = "ANOTHER_ATP_ID"
    private const val A_THIRD_ATP_ID = "A_THIRD_ATP_ID"
    private const val A_FULL_NAME = "A_FULL_NAME"
    private const val ANOTHER_FULL_NAME = "ANOTHER_FULL_NAME"
    private const val A_THIRD_FULL_NAME = "A_THIRD_FULL_NAME"

    private val A_RG_ID = BigDecimal(123)
  }
}