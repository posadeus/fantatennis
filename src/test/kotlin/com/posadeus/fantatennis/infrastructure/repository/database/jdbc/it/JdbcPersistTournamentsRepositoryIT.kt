package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao.TournamentDaoConfiguration
import com.posadeus.fantatennis.domain.infrastructure.PersistTournamentsRepository
import com.posadeus.fantatennis.domain.model.Surface
import com.posadeus.fantatennis.domain.model.TournamentRegistry
import com.posadeus.fantatennis.domain.model.TournamentsCreated.ErrorTournamentsCreation
import com.posadeus.fantatennis.domain.model.TournamentsCreated.SuccessTournamentsCreated
import com.posadeus.fantatennis.domain.model.TournamentsRegistry.FoundTournamentsRegistry
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcPersistTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.tournament.CachedTournamentDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTournamentDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

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
class JdbcPersistTournamentsRepositoryIT {

  @Autowired
  private lateinit var tournamentsCache: Cache<Int, List<JdbcTournamentDto>>

  @Autowired
  private lateinit var tournamentCache: Cache<Int, JdbcTournamentDto>

  @Autowired
  private lateinit var jdbcTournamentDao: TournamentDao

  @Autowired
  private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

  private lateinit var repository: PersistTournamentsRepository

  @BeforeEach
  fun setUp() {

    val cachedTournamentDao = CachedTournamentDao(tournamentsCache, tournamentCache, jdbcTournamentDao)

    repository = JdbcPersistTournamentsRepository(cachedTournamentDao)

    tournamentsCache.invalidateAll()
    tournamentCache.invalidateAll()
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `exception happens during inserts, transaction reverted`() {

    val tournament1 = TournamentRegistry(atpTourId = AN_ATP_TOUR_ID,
                                         tennisTvId = A_TENNIS_TV_ID,
                                         name = A_NAME,
                                         startDate = A_START_DATE,
                                         endDate = AN_END_DATE,
                                         year = A_YEAR,
                                         points = A_POINTS,
                                         surface = A_SURFACE_ENUM,
                                         location = A_LOCATION)
    val tournament2 = TournamentRegistry(atpTourId = 123,
                                         tennisTvId = 123,
                                         name = "FIRST_TOURNAMENT",
                                         startDate = ANOTHER_START_DATE,
                                         endDate = ANOTHER_END_DATE,
                                         year = 2025,
                                         points = ANOTHER_POINTS,
                                         surface = ANOTHER_SURFACE_ENUM,
                                         location = ANOTHER_LOCATION)
    val tournaments = FoundTournamentsRegistry(tournaments = listOf(tournament1, tournament2))

    val expectedMessage = """
      Unexpected error during insert: PreparedStatementCallback; SQL [INSERT INTO TOURNAMENTS
      (ATP_TOUR_ID, TENNIS_TV_ID, NAME, POINTS, LOCATION, SURFACE, `YEAR`, START_DATE, END_DATE)
      VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);]; Duplicate entry '123-123-FIRST_TOURNAMENT-2025' for key 'TOURNAMENTS.TOURNAMENTS_UNIQUE'
    """.trimIndent()

    val expected = ErrorTournamentsCreation(expectedMessage)

    assertThat(repository.persistNewTournaments(tournaments)).isEqualTo(expected)

    val query = """
      SELECT TOURNAMENT_ID, ATP_TOUR_ID, TENNIS_TV_ID, NAME, POINTS, LOCATION, SURFACE, `YEAR`, START_DATE, END_DATE
      FROM TOURNAMENTS
      WHERE ATP_TOUR_ID = :atpTourId;
    """.trimIndent()

    val queryParams = mapOf("atpTourId" to AN_ATP_TOUR_ID)

    assertThat(namedParameterJdbcTemplate.query(query, queryParams, tournamentRowMapper).size).isEqualTo(0)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `persist success for all inputs`() {

    val tournament1 = TournamentRegistry(atpTourId = AN_ATP_TOUR_ID,
                                         tennisTvId = A_TENNIS_TV_ID,
                                         name = A_NAME,
                                         startDate = A_START_DATE,
                                         endDate = AN_END_DATE,
                                         year = A_YEAR,
                                         points = A_POINTS,
                                         surface = A_SURFACE_ENUM,
                                         location = A_LOCATION)
    val tournament2 = TournamentRegistry(atpTourId = ANOTHER_ATP_TOUR_ID,
                                         tennisTvId = ANOTHER_TENNIS_TV_ID,
                                         name = ANOTHER_NAME,
                                         startDate = ANOTHER_START_DATE,
                                         endDate = ANOTHER_END_DATE,
                                         year = A_YEAR,
                                         points = ANOTHER_POINTS,
                                         surface = ANOTHER_SURFACE_ENUM,
                                         location = ANOTHER_LOCATION)
    val tournaments = FoundTournamentsRegistry(tournaments = listOf(tournament1, tournament2))

    val expected = SuccessTournamentsCreated

    assertThat(repository.persistNewTournaments(tournaments)).isEqualTo(expected)

    val query = """
      SELECT TOURNAMENT_ID, ATP_TOUR_ID, TENNIS_TV_ID, NAME, POINTS, LOCATION, SURFACE, `YEAR`, START_DATE, END_DATE
      FROM TOURNAMENTS
      WHERE ATP_TOUR_ID IN (:atpTourIds);
    """.trimIndent()

    val queryParams = mapOf("atpTourIds" to listOf(AN_ATP_TOUR_ID, ANOTHER_ATP_TOUR_ID))

    assertThat(namedParameterJdbcTemplate.query(query, queryParams, tournamentRowMapper).size).isEqualTo(2)
  }

  private val tournamentRowMapper = RowMapper { rs, _ ->
    JdbcTournamentDto(tournamentId = rs.getInt("TOURNAMENT_ID"),
                      atpTourId = rs.getInt("ATP_TOUR_ID"),
                      tennisTvId = rs.getInt("TENNIS_TV_ID"),
                      name = rs.getString("NAME"),
                      points = rs.getInt("POINTS"),
                      location = rs.getString("LOCATION"),
                      surface = rs.getString("SURFACE"),
                      year = rs.getInt("YEAR"),
                      startDate = LocalDate.parse(rs.getString("START_DATE")),
                      endDate = LocalDate.parse(rs.getString("END_DATE")))
  }

  companion object {

    private const val AN_ATP_TOUR_ID = 12345
    private const val ANOTHER_ATP_TOUR_ID = 98765
    private const val A_TENNIS_TV_ID = 23456
    private const val ANOTHER_TENNIS_TV_ID = 76543
    private const val A_YEAR = 1234
    private const val A_NAME = "A_NAME"
    private const val ANOTHER_NAME = "ANOTHER_NAME"
    private const val A_POINTS = 250
    private const val ANOTHER_POINTS = 500
    private const val A_LOCATION = "A_LOCATION"
    private const val ANOTHER_LOCATION = "ANOTHER_LOCATION"
    private const val A_START_DATE = "2025-01-01"
    private const val AN_END_DATE = "2025-01-02"
    private const val ANOTHER_START_DATE = "2025-01-03"
    private const val ANOTHER_END_DATE = "2025-01-04"

    private val A_SURFACE_ENUM = Surface.HARD
    private val ANOTHER_SURFACE_ENUM = Surface.CLAY
  }
}