package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.posadeus.fantatennis.controller.model.team.*
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.Swap.SwapCompleted
import com.posadeus.fantatennis.domain.model.Swap.SwapFailed
import com.posadeus.fantatennis.domain.model.TeamId
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrieveFantaTeamRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcSwapPlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.TeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.team.CachedTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.team.JdbcTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(IntegrationTestConfiguration::class)
class JdbcSwapPlayersRepositoryIT {

  @Autowired
  private lateinit var jdbcTemplate: JdbcTemplate

  @Autowired
  private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

  private val teamCache: Cache<Set<TeamId>, List<JdbcTeamDto>> = Caffeine.newBuilder().build()

  private lateinit var cachedTeamDao: TeamDao
  private lateinit var repository: SwapPlayersRepository

  @BeforeEach
  fun setUp() {

    cachedTeamDao = CachedTeamDao(teamCache, JdbcTeamDao(namedParameterJdbcTemplate))

    repository = JdbcSwapPlayersRepository(JdbcRetrieveFantaTeamRepository(namedParameterJdbcTemplate),
                                           jdbcTemplate,
                                           namedParameterJdbcTemplate,
                                           teamCache)

    teamCache.invalidateAll()
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `no team found`() {

    val expected = SwapFailed

    assertThat(repository.swap(100, ANY_PLAYERS_TO_SWAP)).isEqualTo(expected)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `not all players found`() {

    val playersToSwap = PlayersToSwapDto(remove = PlayersToRemoveDto(playerIds = setOf("NOT_EXISTING_PLAYER_ID")),
                                         add = PlayersToAddDto(playerIds = setOf(AN_EXISTING_PLAYER)))

    val expected = SwapFailed

    assertThat(repository.swap(A_TEAM_ID, playersToSwap)).isEqualTo(expected)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `not all tournaments found`() {

    val remove = PlayersToRemoveDto(playerIds = setOf(AN_EXISTING_PLAYER), endingTournamentId = A_NOT_EXISTING_TOURNAMENT)
    val add = PlayersToAddDto(playerIds = setOf(AN_EXISTING_PLAYER), startingTournamentId = AN_EXISTING_TOURNAMENT)
    val playersToSwap = PlayersToSwapDto(remove = remove, add = add)

    val expected = SwapFailed

    assertThat(repository.swap(A_TEAM_ID, playersToSwap)).isEqualTo(expected)
  }

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `player to remove not found in the team`() {

    val remove = PlayersToRemoveDto(playerIds = setOf(AN_EXISTING_PLAYER_NOT_IN_THE_TEAM), endingTournamentId = AN_EXISTING_TOURNAMENT)
    val add = PlayersToAddDto(playerIds = setOf(AN_EXISTING_PLAYER), startingTournamentId = AN_EXISTING_TOURNAMENT)
    val playersToSwap = PlayersToSwapDto(remove = remove, add = add)

    val expected = SwapFailed

    assertThat(repository.swap(A_TEAM_ID, playersToSwap)).isEqualTo(expected)
  }

  // TODO Find a way to test the fail of the transaction

  @SqlGroup(
      Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD),
      Sql(scripts = ["/test-containers/populate-database.sql"], executionPhase = BEFORE_TEST_METHOD)
  )
  @Test
  fun `swap successful and cached team entries invalidated`() {

    val cachedPlayersBefore = cachedTeamDao.retrieveBy(setOf(A_TEAM_ID)).map { it.playerId }
    assertThat(cachedPlayersBefore).doesNotContain(AN_EXISTING_PLAYER_NOT_IN_THE_TEAM)

    val remove = PlayersToRemoveDto(playerIds = setOf(AN_EXISTING_PLAYER), endingTournamentId = AN_EXISTING_TOURNAMENT)
    val add = PlayersToAddDto(playerIds = setOf(AN_EXISTING_PLAYER_NOT_IN_THE_TEAM), startingTournamentId = AN_EXISTING_TOURNAMENT)
    val playersToSwap = PlayersToSwapDto(remove = remove, add = add)

    assertThat(repository.swap(A_TEAM_ID, playersToSwap)).isEqualTo(SwapCompleted)

    assertThat(teamCache.getIfPresent(setOf(A_TEAM_ID))).isNull()
    assertThat(cachedTeamDao.retrieveBy(setOf(A_TEAM_ID)).map { it.playerId }).contains(AN_EXISTING_PLAYER_NOT_IN_THE_TEAM)
  }

  companion object {

    private const val A_TEAM_ID = 1
    private const val A_NOT_EXISTING_TOURNAMENT = 10000
    private const val AN_EXISTING_TOURNAMENT = 1
    private const val AN_EXISTING_PLAYER = "K5L8"
    private const val AN_EXISTING_PLAYER_NOT_IN_THE_TEAM = "C0D1"

    private val ANY_PLAYERS_TO_SWAP = PlayersToSwapDto(remove = PlayersToRemoveDto(), add = PlayersToAddDto())
  }
}