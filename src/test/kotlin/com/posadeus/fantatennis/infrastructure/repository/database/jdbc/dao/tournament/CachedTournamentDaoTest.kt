package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcTournamentDto.aJdbcTournamentDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class CachedTournamentDaoTest {

  private val cache: Cache<Int, List<JdbcTournamentDto>> = Caffeine.newBuilder().build()
  private val delegate: TournamentDao = mockk()

  private val dao: TournamentDao = CachedTournamentDao(cache, delegate)

  @Test
  fun `no results from cache so it call delegate and cache the result`() {

    val expected = listOf(aJdbcTournamentDto())

    every { delegate.retrieveAllBy(A_YEAR) } returns expected

    assertThat(dao.retrieveAllBy(A_YEAR)).isEqualTo(expected)
    assertThat(dao.retrieveAllBy(A_YEAR)).isEqualTo(expected)

    verify(exactly = 1) { delegate.retrieveAllBy(any()) }
  }

  @Test
  fun `no call to delegate if already in cache`() {

    val expected = listOf(aJdbcTournamentDto())

    cache.put(A_YEAR, expected)

    assertThat(dao.retrieveAllBy(A_YEAR)).isEqualTo(expected)

    verify { delegate wasNot called }
  }

  companion object {

    private const val A_YEAR = 2025
  }
}