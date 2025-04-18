package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.posadeus.fantatennis.domain.infrastructure.RetrieveAllFantaTournamentsRepository
import com.posadeus.fantatennis.domain.model.FantaTournament
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcRetrieveAllFantaTournamentsRepository
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(IntegrationTestConfiguration::class)
class JdbcRetrieveAllFantaTournamentsRepositoryIT {

  @Autowired
  private lateinit var jdbcTemplate: JdbcTemplate

  private lateinit var repository: RetrieveAllFantaTournamentsRepository

  @BeforeEach
  fun setUp() {

    repository = JdbcRetrieveAllFantaTournamentsRepository(jdbcTemplate)
  }

  @Sql(scripts = ["/test-containers/clear-db.sql"], executionPhase = BEFORE_TEST_METHOD)
  @Test
  fun `no tournaments found`() {

    val expected = emptySet<FantaTournament>()

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
    val expected = setOf(element1, element2, element3)

    assertThat(repository.retrieve()).isEqualTo(expected)
  }
}