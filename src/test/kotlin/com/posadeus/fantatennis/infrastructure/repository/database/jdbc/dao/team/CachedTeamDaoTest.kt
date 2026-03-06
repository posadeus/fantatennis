package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.team

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.domain.model.TeamId
import com.posadeus.fantatennis.infrastructure.assertThrowsWithMessage
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcTeamDto.aJdbcTeamDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CachedTeamDaoTest {

  private val cache: Cache<Set<TeamId>, List<JdbcTeamDto>> = Caffeine.newBuilder().build()
  private val delegate: TeamDao = mockk()

  private val dao: TeamDao = CachedTeamDao(cache, delegate)

  @Nested
  inner class Retrieve {

    @Test
    fun `no results from cache so it call delegate and cache the result`() {

      val teamIds = setOf(A_TEAM_ID, ANOTHER_TEAM_ID)

      val expected = listOf(aJdbcTeamDto(teamId = A_TEAM_ID), aJdbcTeamDto(teamId = ANOTHER_TEAM_ID))

      every { delegate.retrieveBy(teamIds) } returns expected

      assertThat(dao.retrieveBy(teamIds)).isEqualTo(expected)
      assertThat(dao.retrieveBy(teamIds)).isEqualTo(expected)

      verify(exactly = 1) { delegate.retrieveBy(teamIds) }
    }

    @Test
    fun `no call to delegate if already in cache`() {

      val teamIds = setOf(A_TEAM_ID, ANOTHER_TEAM_ID)

      val expected = listOf(aJdbcTeamDto(teamId = A_TEAM_ID), aJdbcTeamDto(teamId = ANOTHER_TEAM_ID))

      cache.put(teamIds, expected)

      assertThat(dao.retrieveBy(teamIds)).isEqualTo(expected)

      verify { delegate wasNot called }
    }
  }

  @Nested
  inner class Persist {

    @Test
    fun `throws any error from delegate`() {

      val jdbcTeams = setOf(aJdbcTeamDto(teamId = A_TEAM_ID), aJdbcTeamDto(teamId = ANOTHER_TEAM_ID))

      val expectedMessage = "Error!!"

      every { delegate.persist(jdbcTeams) } throws RuntimeException(expectedMessage)

      assertThrowsWithMessage<RuntimeException>(expectedMessage) { dao.persist(jdbcTeams) }
    }

    @Test
    fun `persisted by delegate and flush cache`() {

      val teamIds = setOf(A_TEAM_ID, ANOTHER_TEAM_ID)

      val jdbcTeams = setOf(aJdbcTeamDto(teamId = A_TEAM_ID), aJdbcTeamDto(teamId = ANOTHER_TEAM_ID))

      cache.put(teamIds, listOf(aJdbcTeamDto(teamId = A_TEAM_ID), aJdbcTeamDto(teamId = ANOTHER_TEAM_ID)))

      assertThat(cache.getIfPresent(teamIds)!!.size).isEqualTo(2)

      every { delegate.persist(jdbcTeams) } returns Unit

      dao.persist(jdbcTeams)

      verify(exactly = 1) { delegate.persist(jdbcTeams) }

      assertThat(cache.getIfPresent(teamIds)?.size).isEqualTo(null)
    }
  }

  companion object {

    private const val A_TEAM_ID = 123
    private const val ANOTHER_TEAM_ID = 35345
  }
}