package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcPlayerPointsDto.aJdbcPlayerPointsDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CachedPlayerPointsDaoTest {

  private val cacheById: Cache<Int, List<JdbcPlayerPointsDto>> = Caffeine.newBuilder().build()
  private val cacheByYear: Cache<Int, List<JdbcPlayerPointsDto>> = Caffeine.newBuilder().build()
  private val delegate: PlayerPointsDao = mockk()

  private val dao: PlayerPointsDao = CachedPlayerPointsDao(cacheById, cacheByYear, delegate)

  @Nested
  inner class RetrieveByTournamentId {

    @Test
    fun `no results from cache so it call delegate and cache the result`() {

      val expected = listOf(aJdbcPlayerPointsDto())

      every { delegate.retrieveByTournamentId(A_TOURNAMENT_ID) } returns expected

      assertThat(dao.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)
      assertThat(dao.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)

      verify(exactly = 1) { delegate.retrieveByTournamentId(A_TOURNAMENT_ID) }
    }

    @Test
    fun `no call to delegate if already in cache`() {

      val expected = listOf(aJdbcPlayerPointsDto())

      cacheById.put(A_TOURNAMENT_ID, expected)

      assertThat(dao.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)

      verify { delegate wasNot called }
    }
  }

  @Nested
  inner class RetrieveByTournamentYear {

    @Test
    fun `no results from cache so it call delegate and cache the result`() {

      val expected = listOf(aJdbcPlayerPointsDto())

      every { delegate.retrieveByTournamentYear(A_YEAR) } returns expected

      assertThat(dao.retrieveByTournamentYear(A_YEAR)).isEqualTo(expected)
      assertThat(dao.retrieveByTournamentYear(A_YEAR)).isEqualTo(expected)

      verify(exactly = 1) { delegate.retrieveByTournamentYear(A_YEAR) }
    }

    @Test
    fun `no call to delegate if already in cache`() {

      val expected = listOf(aJdbcPlayerPointsDto())

      cacheByYear.put(A_YEAR, expected)

      assertThat(dao.retrieveByTournamentYear(A_YEAR)).isEqualTo(expected)

      verify { delegate wasNot called }
    }
  }

  @Nested
  inner class PersistAll {

    @Test
    fun `throws any error from delegate`() {

      val expectedMessage = "Error!!"

      every { delegate.persistAll(PLAYERS) } throws RuntimeException(expectedMessage)

      assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.persistAll(PLAYERS) }
    }

    @Test
    fun `persisted by delegate and flush cache`() {

      cacheById.put(A_TOURNAMENT_ID, listOf(aJdbcPlayerPointsDto()))
      cacheByYear.put(A_YEAR, listOf(aJdbcPlayerPointsDto()))

      assertThat(cacheById.getIfPresent(A_TOURNAMENT_ID)!!.size).isEqualTo(1)
      assertThat(cacheByYear.getIfPresent(A_YEAR)!!.size).isEqualTo(1)

      every { delegate.persistAll(PLAYERS) } returns Unit

      dao.persistAll(PLAYERS)

      verify(exactly = 1) { delegate.persistAll(PLAYERS) }

      assertThat(cacheById.getIfPresent(A_TOURNAMENT_ID)?.size).isEqualTo(null)
      assertThat(cacheByYear.getIfPresent(A_YEAR)?.size).isEqualTo(null)
    }
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1
    private const val A_YEAR = 2022

    private val PLAYERS = listOf(aJdbcPlayerPointsDto(), aJdbcPlayerPointsDto())
  }
}