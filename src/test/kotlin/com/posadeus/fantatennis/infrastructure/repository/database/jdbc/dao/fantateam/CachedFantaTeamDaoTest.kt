package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantateam

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcFantaTeamDto.aJdbcFantaTeamDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CachedFantaTeamDaoTest {

  private val cache: Cache<Int, JdbcFantaTeamDto> = Caffeine.newBuilder().build()
  private val delegate: FantaTeamDao = mockk()

  private val dao: FantaTeamDao = CachedFantaTeamDao(cache, delegate)

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

    AssertionsForInterfaceTypes.assertThat(dao.retrieveBy(A_TEAM_ID)).isEqualTo(expected)
    AssertionsForInterfaceTypes.assertThat(dao.retrieveBy(A_TEAM_ID)).isEqualTo(expected)

    verify(exactly = 1) { delegate.retrieveBy(any()) }
  }

  @Test
  fun `no call to delegate if already in cache`() {

    val expected = aJdbcFantaTeamDto(teamId = A_TEAM_ID)

    cache.put(A_TEAM_ID, expected)

    AssertionsForInterfaceTypes.assertThat(dao.retrieveBy(A_TEAM_ID)).isEqualTo(expected)

    verify { delegate wasNot called }
  }

  companion object {

    private const val A_TEAM_ID = 123
  }
}