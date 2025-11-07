package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.player

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcPlayerDto.aJdbcPlayerDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CachedPlayerDaoTest {

  private val cache: Cache<Unit, List<JdbcPlayerDto>> = Caffeine.newBuilder().build()
  private val delegate: PlayerDao = mockk()

  private val dao: PlayerDao = CachedPlayerDao(cache, delegate)

  @Nested
  inner class Retrieve {

    @Test
    fun `no results from cache so it call delegate and cache the result`() {

      val expected = listOf(aJdbcPlayerDto())

      every { delegate.retrieveAll() } returns expected

      assertThat(dao.retrieveAll()).isEqualTo(expected)
      assertThat(dao.retrieveAll()).isEqualTo(expected)

      verify(exactly = 1) { delegate.retrieveAll() }
    }

    @Test
    fun `no call to delegate if already in cache`() {

      val expected = listOf(aJdbcPlayerDto())

      cache.put(Unit, expected)

      assertThat(dao.retrieveAll()).isEqualTo(expected)

      verify { delegate wasNot called }
    }
  }

  @Nested
  inner class Persist {

    @Test
    fun `throws any error from delegate`() {

      val expectedMessage = "Error!!"

      every { delegate.persistAll(PLAYERS) } throws RuntimeException(expectedMessage)

      assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.persistAll(PLAYERS) }
    }

    @Test
    fun `persisted by delegate and flush cache`() {

      cache.put(Unit, listOf(aJdbcPlayerDto()))

      assertThat(cache.getIfPresent(Unit)!!.size).isEqualTo(1)

      every { delegate.persistAll(PLAYERS) } returns Unit

      dao.persistAll(PLAYERS)

      verify(exactly = 1) { delegate.persistAll(PLAYERS) }

      assertThat(cache.getIfPresent(Unit)?.size).isEqualTo(null)
    }
  }

  companion object {

    private val PLAYERS = emptySet<JdbcPlayerDto>()
  }
}