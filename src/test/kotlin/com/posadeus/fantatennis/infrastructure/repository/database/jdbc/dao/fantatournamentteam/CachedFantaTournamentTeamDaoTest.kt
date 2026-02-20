package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournamentteam

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcFantaTournamentTeamDto.aJdbcFantaTournamentTeamDto
import com.posadeus.fantatennis.infrastructure.repository.exception.NoInsertException
import io.mockk.*
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CachedFantaTournamentTeamDaoTest {

  private val cache: Cache<Int, JdbcFantaTournamentTeamDto> = Caffeine.newBuilder().build()
  private val delegate: FantaTournamentTeamDao = mockk()

  private val dao: FantaTournamentTeamDao = CachedFantaTournamentTeamDao(cache, delegate)

  @Nested
  inner class Persist {

    @Test
    fun `throws any error from delegate`() {

      val fantaTournamentTeam = aJdbcFantaTournamentTeamDto(teamId = A_TEAM_ID, fantaTournamentId = A_FANTA_TOURNAMENT_ID)

      val expectedMessage = "Error!!"

      every { delegate.persist(fantaTournamentTeam) } throws NoInsertException(expectedMessage)

      assertThrowsWithMessage<NoInsertException>(expectedMessage) { dao.persist(fantaTournamentTeam) }
    }

    @Test
    fun `persisted by delegate and cache untouched`() {

      val fantaTournamentTeam = aJdbcFantaTournamentTeamDto(teamId = A_TEAM_ID, fantaTournamentId = A_FANTA_TOURNAMENT_ID)

      val cacheData = aJdbcFantaTournamentTeamDto(fantaTournamentId = ANOTHER_FANTA_TOURNAMENT_ID)
      cache.put(ANOTHER_FANTA_TOURNAMENT_ID, cacheData)

      assertThat(cache.getIfPresent(ANOTHER_FANTA_TOURNAMENT_ID)).isEqualTo(cacheData)

      every { delegate.persist(fantaTournamentTeam) } returns Unit

      dao.persist(fantaTournamentTeam)

      verify(exactly = 1) { delegate.persist(fantaTournamentTeam) }

      assertThat(cache.getIfPresent(ANOTHER_FANTA_TOURNAMENT_ID)).isEqualTo(cacheData)
    }
  }

  @Nested
  inner class Retrieve {

    @Test
    fun `no result from cache so it call delegate and cache the result`() {

      val expected = aJdbcFantaTournamentTeamDto(fantaTournamentId = A_FANTA_TOURNAMENT_ID)

      every { delegate.retrieveBy(A_FANTA_TOURNAMENT_ID) } returns expected

      assertThat(dao.retrieveBy(A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)
      assertThat(dao.retrieveBy(A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)

      verify(exactly = 1) { delegate.retrieveBy(A_FANTA_TOURNAMENT_ID) }
    }

    @Test
    fun `no call to delegate if already in cache`() {

      val expected = aJdbcFantaTournamentTeamDto(fantaTournamentId = A_FANTA_TOURNAMENT_ID)

      cache.put(A_FANTA_TOURNAMENT_ID, expected)

      assertThat(dao.retrieveBy(A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)

      verify { delegate wasNot called }
    }
  }

  companion object {

    private const val A_TEAM_ID = 123
    private const val A_FANTA_TOURNAMENT_ID = 1566
    private const val ANOTHER_FANTA_TOURNAMENT_ID = 345
  }
}