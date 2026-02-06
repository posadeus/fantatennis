package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao.FantaTournamentDaoConfiguration
import com.posadeus.fantatennis.controller.model.fantatournament.FantaTournamentToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.CreateFantaTournamentRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcCreateFantaTournamentRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournament.CachedFantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.TestJdbcFantaTournamentDto.aJdbcFantaTournamentDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(IntegrationTestConfiguration::class, FantaTournamentDaoConfiguration::class)
@TestPropertySource(properties = [
  "caches.caffeine.fanta-tournament-cache.expire-after-write-duration=10080",
  "caches.caffeine.fanta-tournament-cache.expire-after-access-duration=10080",
  "caches.caffeine.fanta-tournament-cache.maximum-size=1000",
  "caches.caffeine.fanta-tournaments-cache.expire-after-write-duration=10080",
  "caches.caffeine.fanta-tournaments-cache.expire-after-access-duration=10080",
  "caches.caffeine.fanta-tournaments-cache.maximum-size=1000",
])
class JdbcCreateFantaTournamentRepositoryIT {

  @Autowired
  private lateinit var fantaTournamentsCache: Cache<Unit, List<JdbcFantaTournamentDto>>

  @Autowired
  private lateinit var fantaTournamentCache: Cache<Int, JdbcFantaTournamentDto>

  @Autowired
  private lateinit var jdbcFantaTournamentDao: FantaTournamentDao

  private lateinit var repository: CreateFantaTournamentRepository

  @BeforeEach
  fun setUp() {

    val cachedFantaTournamentDao = CachedFantaTournamentDao(fantaTournamentCache, fantaTournamentsCache, jdbcFantaTournamentDao)

    repository = JdbcCreateFantaTournamentRepository(cachedFantaTournamentDao)

    fantaTournamentCache.invalidateAll()
    fantaTournamentsCache.invalidateAll()
  }

  @Test
  fun `tournament successfully created`() {

    fantaTournamentsCache.put(Unit, listOf(aJdbcFantaTournamentDto()))

    assertThat(fantaTournamentsCache.getIfPresent(Unit)?.size).isEqualTo(1)

    val dto = FantaTournamentToCreateDto(startingTournamentId = 2,
                                         endingTournamentId = 10,
                                         tournamentYear = 2022)

    val expected = ValidFantaTournament(id = 1,
                                        startingTournamentId = 2,
                                        endingTournamentId = 10,
                                        tournamentYear = 2022)

    assertThat(repository.create(dto)).isEqualTo(expected)
    assertThat(fantaTournamentsCache.getIfPresent(Unit)?.size).isEqualTo(null)
  }
}