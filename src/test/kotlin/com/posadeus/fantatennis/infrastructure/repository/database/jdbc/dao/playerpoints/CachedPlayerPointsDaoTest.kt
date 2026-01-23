package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcPlayerPointsDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcPlayerPointsDto.aJdbcPlayerPointsDto
import io.mockk.*
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class CachedPlayerPointsDaoTest {

  private val cache: Cache<Int, List<JdbcPlayerPointsDto>> = Caffeine.newBuilder().build()
  private val delegate: PlayerPointsDao = mockk()

  private val dao: PlayerPointsDao = CachedPlayerPointsDao(cache, delegate)

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

    cache.put(A_TOURNAMENT_ID, expected)

    assertThat(dao.retrieveByTournamentId(A_TOURNAMENT_ID)).isEqualTo(expected)

    verify { delegate wasNot called }
  }

  companion object {

    private const val A_TOURNAMENT_ID = 1
  }
}