package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantateam

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcFantaTeamDto.aJdbcFantaTeamDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.*

class CachedFantaTeamDaoTest {

  private val cache: Cache<Int, JdbcFantaTeamDto> = Caffeine.newBuilder().build()
  private val delegate: FantaTeamDao = mockk()

  private val dao: FantaTeamDao = CachedFantaTeamDao(cache, delegate)

  @Nested
  inner class Retrieve {

    @Test
    fun `no results form cache and exception from the delegate not stored into cache`() {

      every { delegate.retrieveBy(A_TEAM_ID) } throws RuntimeException()

      assertThrows<RuntimeException> { dao.retrieveBy(A_TEAM_ID) }
      assertThrows<RuntimeException> { dao.retrieveBy(A_TEAM_ID) }

      verify(exactly = 2) { delegate.retrieveBy(any()) }
    }

    @Test
    fun `no results from cache so it call delegate and cache the result`() {

      val expected = aJdbcFantaTeamDto(teamId = A_TEAM_ID)

      every { delegate.retrieveBy(A_TEAM_ID) } returns expected

      assertThat(dao.retrieveBy(A_TEAM_ID)).isEqualTo(expected)
      assertThat(dao.retrieveBy(A_TEAM_ID)).isEqualTo(expected)

      verify(exactly = 1) { delegate.retrieveBy(any()) }
    }

    @Test
    fun `no call to delegate if already in cache`() {

      val expected = aJdbcFantaTeamDto(teamId = A_TEAM_ID)

      cache.put(A_TEAM_ID, expected)

      assertThat(dao.retrieveBy(A_TEAM_ID)).isEqualTo(expected)

      verify { delegate wasNot called }
    }
  }

  @Nested
  inner class Persist {

    @Test
    fun `throws any error from delegate`() {

      val expectedMessage = "Error!!"

      every { delegate.persist(AN_OWNER_ID) } throws RuntimeException(expectedMessage)

      assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.persist(AN_OWNER_ID) }
    }

    @Test
    fun `persisted by delegate and flush cache`() {

      every { delegate.persist(AN_OWNER_ID) } returns A_TEAM_ID

      assertThat(dao.persist(AN_OWNER_ID)).isEqualTo(A_TEAM_ID)

      verify(exactly = 1) { delegate.persist(AN_OWNER_ID) }
    }
  }

  companion object {

    private const val A_TEAM_ID = 123
    private const val AN_OWNER_ID = "AN_OWNER_ID"
  }
}