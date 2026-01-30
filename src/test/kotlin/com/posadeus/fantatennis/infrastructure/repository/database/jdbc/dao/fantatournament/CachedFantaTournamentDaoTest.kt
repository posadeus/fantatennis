package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournament

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcFantaTournamentDto.aJdbcFantaTournamentDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CachedFantaTournamentDaoTest {

  private val cache: Cache<Int, JdbcFantaTournamentDto> = Caffeine.newBuilder().build()
  private val delegate: FantaTournamentDao = mockk()

  private val dao: FantaTournamentDao = CachedFantaTournamentDao(cache, delegate)

  @Test
  fun `no results form cache and exception from the delegate not stored into cache`() {

    every { delegate.retrieveBy(A_FANTA_TOURNAMENT_ID) } throws RuntimeException()

    assertThrows<RuntimeException> { dao.retrieveBy(A_FANTA_TOURNAMENT_ID) }
    assertThrows<RuntimeException> { dao.retrieveBy(A_FANTA_TOURNAMENT_ID) }

    verify(exactly = 2) { delegate.retrieveBy(any()) }
  }

  @Test
  fun `no results from cache so it call delegate and cache the result`() {

    val expected = aJdbcFantaTournamentDto(id = A_FANTA_TOURNAMENT_ID)

    every { delegate.retrieveBy(A_FANTA_TOURNAMENT_ID) } returns expected

    assertThat(dao.retrieveBy(A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)
    assertThat(dao.retrieveBy(A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)

    verify(exactly = 1) { delegate.retrieveBy(any()) }
  }

  @Test
  fun `no call to delegate if already in cache`() {

    val expected = aJdbcFantaTournamentDto(id = A_FANTA_TOURNAMENT_ID)

    cache.put(A_FANTA_TOURNAMENT_ID, expected)

    assertThat(dao.retrieveBy(A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)

    verify { delegate wasNot called }
  }

  companion object {

    private const val A_FANTA_TOURNAMENT_ID = 123
  }
}