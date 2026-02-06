package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournament

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.NewJdbcFantaTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcFantaTournamentDto.aJdbcFantaTournamentDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.*

class CachedFantaTournamentDaoTest {

  private val fantaTournamentCache: Cache<Int, JdbcFantaTournamentDto> = Caffeine.newBuilder().build()
  private val fantaTournamentsCache: Cache<Unit, List<JdbcFantaTournamentDto>> = Caffeine.newBuilder().build()
  private val delegate: FantaTournamentDao = mockk()

  private val dao: FantaTournamentDao = CachedFantaTournamentDao(fantaTournamentCache, fantaTournamentsCache, delegate)

  @Nested
  inner class RetrieveBy {

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

      fantaTournamentCache.put(A_FANTA_TOURNAMENT_ID, expected)

      assertThat(dao.retrieveBy(A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)

      verify { delegate wasNot called }
    }
  }

  @Nested
  inner class RetrieveAll {

    @Test
    fun `no results form cache and exception from the delegate not stored into cache`() {

      every { delegate.retrieveAll() } throws RuntimeException()

      assertThrows<RuntimeException> { dao.retrieveAll() }
      assertThrows<RuntimeException> { dao.retrieveAll() }

      verify(exactly = 2) { delegate.retrieveAll() }
    }

    @Test
    fun `no results from cache so it call delegate and cache the result`() {

      val expected = listOf(aJdbcFantaTournamentDto(), aJdbcFantaTournamentDto())

      every { delegate.retrieveAll() } returns expected

      assertThat(dao.retrieveAll()).isEqualTo(expected)
      assertThat(dao.retrieveAll()).isEqualTo(expected)

      verify(exactly = 1) { delegate.retrieveAll() }
    }

    @Test
    fun `do not store empty results in the cache`() {

      val expected = emptyList<JdbcFantaTournamentDto>()

      every { delegate.retrieveAll() } returns expected

      assertThat(dao.retrieveAll()).isEqualTo(expected)
      assertThat(dao.retrieveAll()).isEqualTo(expected)

      verify(exactly = 2) { delegate.retrieveAll() }
    }

    @Test
    fun `no call to delegate if already in cache`() {

      val expected = listOf(aJdbcFantaTournamentDto(), aJdbcFantaTournamentDto())

      fantaTournamentsCache.put(Unit, expected)

      assertThat(dao.retrieveAll()).isEqualTo(expected)

      verify { delegate wasNot called }
    }
  }

  @Nested
  inner class Persist {

    @Test
    fun `throws any error from delegate`() {

      fantaTournamentsCache.put(Unit, listOf(aJdbcFantaTournamentDto()))

      assertThat(fantaTournamentsCache.getIfPresent(Unit)?.size).isEqualTo(1)

      val dto = NewJdbcFantaTournamentDto(startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                          endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                          year = A_TOURNAMENT_YEAR)

      val expectedMessage = "Error!!"

      every { delegate.persist(dto) } throws RuntimeException(expectedMessage)

      assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.persist(dto) }
      assertThat(fantaTournamentsCache.getIfPresent(Unit)?.size).isEqualTo(1)
    }

    @Test
    fun `persisted by delegate and flush cache`() {

      fantaTournamentsCache.put(Unit, listOf(aJdbcFantaTournamentDto()))

      assertThat(fantaTournamentsCache.getIfPresent(Unit)?.size).isEqualTo(1)

      val dto = NewJdbcFantaTournamentDto(startingTournamentId = A_STARTING_TOURNAMENT_ID,
                                          endingTournamentId = AN_ENDING_TOURNAMENT_ID,
                                          year = A_TOURNAMENT_YEAR)

      every { delegate.persist(dto) } returns A_FANTA_TOURNAMENT_ID

      dao.persist(dto)

      verify(exactly = 1) { delegate.persist(dto) }

      assertThat(fantaTournamentsCache.getIfPresent(Unit)?.size).isEqualTo(null)
    }
  }

  companion object {

    private const val A_FANTA_TOURNAMENT_ID = 123
    private const val A_STARTING_TOURNAMENT_ID = 3
    private const val AN_ENDING_TOURNAMENT_ID = 5
    private const val A_TOURNAMENT_YEAR = 2026
  }
}