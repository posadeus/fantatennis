package com.posadeus.fantatennis.infrastructure.repository.database.it

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao.FantaTournamentDaoConfiguration
import com.posadeus.fantatennis.domain.infrastructure.RetrieveAllFantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournaments.Valid
import com.posadeus.fantatennis.infrastructure.repository.database.SqlRetrieveAllFantaTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.FantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.fantatournament.CachedFantaTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcFantaTournamentDto
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it.IntegrationTestConfiguration
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup
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
class SqlRetrieveAllFantaTournamentsRepositoryIT {

  @Autowired
  private lateinit var fantaTournamentsCache: Cache<Unit, List<JdbcFantaTournamentDto>>

  @Autowired
  private lateinit var fantaTournamentCache: Cache<Int, JdbcFantaTournamentDto>

  @Autowired
  private lateinit var jdbcFantaTournamentDao: FantaTournamentDao

  private lateinit var repository: RetrieveAllFantaTournamentsRepository

  @BeforeEach
  fun setUp() {

    val cachedFantaTournamentDao = CachedFantaTournamentDao(fantaTournamentCache, fantaTournamentsCache, jdbcFantaTournamentDao)

    repository = SqlRetrieveAllFantaTournamentsRepository(cachedFantaTournamentDao)

    fantaTournamentCache.invalidateAll()
    fantaTournamentsCache.invalidateAll()
  }

  @Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD)
  @Test
  fun `no tournaments found`() {

    val expected = Valid(emptySet())

    assertThat(repository.retrieve()).isEqualTo(expected)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `retrieve all available fanta tournaments`() {

    val element1 = ValidFantaTournament(id = 1,
                                        startingTournamentId = 1,
                                        endingTournamentId = 3,
                                        tournamentYear = 2025)
    val element2 = ValidFantaTournament(id = 2,
                                        startingTournamentId = 2,
                                        endingTournamentId = 3,
                                        tournamentYear = 2025)
    val element3 = ValidFantaTournament(id = 3,
                                        startingTournamentId = 1,
                                        endingTournamentId = 4,
                                        tournamentYear = 2026)
    val expected = Valid(setOf(element1, element2, element3))

    assertThat(repository.retrieve()).isEqualTo(expected)
  }
}