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

  private val cacheByTournamentId: Cache<Int, List<JdbcFantaTournamentTeamDto>> = Caffeine.newBuilder().build()
  private val cacheByTeamId: Cache<Int, JdbcFantaTournamentTeamDto> = Caffeine.newBuilder().build()
  private val delegate: FantaTournamentTeamDao = mockk()

  private val dao: FantaTournamentTeamDao = CachedFantaTournamentTeamDao(cacheByTournamentId, cacheByTeamId, delegate)

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

      val cacheData = aJdbcFantaTournamentTeamDto(teamId = A_TEAM_ID, fantaTournamentId = ANOTHER_FANTA_TOURNAMENT_ID)
      cacheByTournamentId.put(ANOTHER_FANTA_TOURNAMENT_ID, listOf(cacheData))
      cacheByTeamId.put(A_TEAM_ID, cacheData)

      assertThat(cacheByTournamentId.getIfPresent(ANOTHER_FANTA_TOURNAMENT_ID)).isEqualTo(listOf(cacheData))
      assertThat(cacheByTeamId.getIfPresent(A_TEAM_ID)).isEqualTo(cacheData)

      every { delegate.persist(fantaTournamentTeam) } returns Unit

      dao.persist(fantaTournamentTeam)

      verify(exactly = 1) { delegate.persist(fantaTournamentTeam) }

      assertThat(cacheByTournamentId.getIfPresent(ANOTHER_FANTA_TOURNAMENT_ID)).isEqualTo(listOf(cacheData))
      assertThat(cacheByTeamId.getIfPresent(A_TEAM_ID)).isEqualTo(cacheData)
    }
  }

  @Nested
  inner class RetrieveByTournamentId {

    @Test
    fun `no result from cache so it call delegate and cache the result`() {

      val expected = listOf(aJdbcFantaTournamentTeamDto(fantaTournamentId = A_FANTA_TOURNAMENT_ID))

      every { delegate.retrieveByFantaTournamentId(A_FANTA_TOURNAMENT_ID) } returns expected

      assertThat(dao.retrieveByFantaTournamentId(A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)
      assertThat(dao.retrieveByFantaTournamentId(A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)

      verify(exactly = 1) { delegate.retrieveByFantaTournamentId(A_FANTA_TOURNAMENT_ID) }
    }

    @Test
    fun `no call to delegate if already in cache`() {

      val expected = listOf(aJdbcFantaTournamentTeamDto(fantaTournamentId = A_FANTA_TOURNAMENT_ID))

      cacheByTournamentId.put(A_FANTA_TOURNAMENT_ID, expected)

      assertThat(dao.retrieveByFantaTournamentId(A_FANTA_TOURNAMENT_ID)).isEqualTo(expected)

      verify { delegate wasNot called }
    }
  }

  @Nested
  inner class RetrieveByTeamId {

    @Test
    fun `no result from cache so it call delegate and cache the result`() {

      val expected = aJdbcFantaTournamentTeamDto(teamId = A_TEAM_ID)

      every { delegate.retrieveByTeamId(A_TEAM_ID) } returns expected

      assertThat(dao.retrieveByTeamId(A_TEAM_ID)).isEqualTo(expected)
      assertThat(dao.retrieveByTeamId(A_TEAM_ID)).isEqualTo(expected)

      verify(exactly = 1) { delegate.retrieveByTeamId(A_TEAM_ID) }
    }

    @Test
    fun `no call to delegate if already in cache`() {

      val expected = aJdbcFantaTournamentTeamDto(teamId = A_TEAM_ID)

      cacheByTeamId.put(A_TEAM_ID, expected)

      assertThat(dao.retrieveByTeamId(A_TEAM_ID)).isEqualTo(expected)

      verify { delegate wasNot called }
    }
  }

  companion object {

    private const val A_TEAM_ID = 123
    private const val A_FANTA_TOURNAMENT_ID = 1566
    private const val ANOTHER_FANTA_TOURNAMENT_ID = 345
  }
}