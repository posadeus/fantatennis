package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao.TournamentDaoConfiguration
import com.posadeus.fantatennis.domain.infrastructure.RetrieveTournamentsRepository
import com.posadeus.fantatennis.domain.model.TestTournament.aTournament
import com.posadeus.fantatennis.domain.model.Tournament
import com.posadeus.fantatennis.domain.model.Tournament.NotFoundTournament
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrieveTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament.CachedTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(IntegrationTestConfiguration::class, TournamentDaoConfiguration::class)
@TestPropertySource(properties = [
  "caches.caffeine.tournaments-cache.expire-after-write-duration=1440",
  "caches.caffeine.tournaments-cache.expire-after-access-duration=1440",
  "caches.caffeine.tournaments-cache.maximum-size=300",
  "caches.caffeine.tournament-cache.expire-after-write-duration=1440",
  "caches.caffeine.tournament-cache.expire-after-access-duration=1440",
  "caches.caffeine.tournament-cache.maximum-size=300"
])
class JdbcRetrieveTournamentsRepositoryIT {

  @Autowired
  private lateinit var tournamentsCache: Cache<Int, List<JdbcTournamentDto>>

  @Autowired
  private lateinit var tournamentCache: Cache<Int, JdbcTournamentDto>

  @Autowired
  private lateinit var jdbcTournamentDao: TournamentDao

  private lateinit var repository: RetrieveTournamentsRepository

  @BeforeEach
  fun setUp() {

    val cachedTournamentDao = CachedTournamentDao(tournamentsCache, tournamentCache, jdbcTournamentDao)

    repository = JdbcRetrieveTournamentsRepository(cachedTournamentDao)

    tournamentsCache.invalidateAll()
    tournamentCache.invalidateAll()
  }

  @Nested
  inner class AllTournaments {

    @Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD)
    @Test
    fun `no tournaments found`() {

      val expected = emptyList<Tournament>()

      assertThat(repository.retrieveAllBy(A_YEAR)).isEqualTo(expected)
    }

    @SqlGroup(
        Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
    )
    @Test
    fun `tournaments found`() {

      val tournament1 = aTournament(id = 1,
                                    name = "FIRST_TOURNAMENT",
                                    tennisTvId = 123,
                                    points = 1000,
                                    year = 2025)
      val tournament2 = aTournament(id = 2,
                                    name = "SECOND_TOURNAMENT",
                                    tennisTvId = 456,
                                    points = 250,
                                    year = 2025)
      val tournament3 = aTournament(id = 3,
                                    name = "THIRD_TOURNAMENT",
                                    tennisTvId = 789,
                                    points = 500,
                                    year = 2025)
      val tournament4 = aTournament(id = 4,
                                    name = "FOURTH_TOURNAMENT",
                                    tennisTvId = 12,
                                    points = 250,
                                    year = 2025)
      val expected = listOf(tournament1, tournament2, tournament3, tournament4)

      assertThat(repository.retrieveAllBy(2025)).isEqualTo(expected)
    }
  }

  @Nested
  inner class ByTournamentId {

    @Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD)
    @Test
    fun `no tournament found`() {

      val expected = NotFoundTournament

      assertThat(repository.retrieveBy(A_TOURNAMENT_ID)).isEqualTo(expected)
    }

    @SqlGroup(
        Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
        Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
    )
    @Test
    fun `tournaments found`() {

      val expected = aTournament(id = 1,
                                 name = "FIRST_TOURNAMENT",
                                 tennisTvId = 123,
                                 points = 1000,
                                 year = 2025)

      assertThat(repository.retrieveBy(A_TOURNAMENT_ID)).isEqualTo(expected)
    }
  }

  companion object {

    private const val A_YEAR = 2000
    private const val A_TOURNAMENT_ID = 1
  }
}