package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcTournamentDto.aJdbcTournamentDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.*

class CachedTournamentDaoTest {

  private val tournamentsCache: Cache<Int, List<JdbcTournamentDto>> = Caffeine.newBuilder().build()
  private val tournamentCache: Cache<Int, JdbcTournamentDto> = Caffeine.newBuilder().build()
  private val delegate: TournamentDao = mockk()

  private val dao: TournamentDao = CachedTournamentDao(tournamentsCache, tournamentCache, delegate)

  @Nested
  inner class ByYear {

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

      tournamentsCache.put(A_YEAR, expected)

      assertThat(dao.retrieveAllBy(A_YEAR)).isEqualTo(expected)

      verify { delegate wasNot called }
    }
  }

  @Nested
  inner class ById {

    @Test
    fun `no results form cache and exception from the delegate not stored into cache`() {

      every { delegate.retrieveBy(AN_ID) } throws RuntimeException()

      assertThrows<RuntimeException> { dao.retrieveBy(AN_ID) }
      assertThrows<RuntimeException> { dao.retrieveBy(AN_ID) }

      verify(exactly = 2) { delegate.retrieveBy(any()) }
    }

    @Test
    fun `no results from cache so it call delegate and cache the result`() {

      val expected = aJdbcTournamentDto(tournamentId = AN_ID)

      every { delegate.retrieveBy(AN_ID) } returns expected

      assertThat(dao.retrieveBy(AN_ID)).isEqualTo(expected)
      assertThat(dao.retrieveBy(AN_ID)).isEqualTo(expected)

      verify(exactly = 1) { delegate.retrieveBy(any()) }
    }

    @Test
    fun `no call to delegate if already in cache`() {

      val expected = aJdbcTournamentDto(tournamentId = AN_ID)

      tournamentCache.put(AN_ID, expected)

      assertThat(dao.retrieveBy(AN_ID)).isEqualTo(expected)

      verify { delegate wasNot called }
    }
  }

  companion object {

    private const val A_YEAR = 2025
    private const val AN_ID = 123
  }
}