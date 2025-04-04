package com.posadeus.fantatennis.infrastructure.repository.database.jdbc

import com.posadeus.fantatennis.controller.model.tournament.TournamentToCreateDto
import com.posadeus.fantatennis.domain.infrastructure.CreateFantaTournamentRepository
import com.posadeus.fantatennis.domain.model.FantaTournament.ValidFantaTournament
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.MountableFile
import javax.sql.DataSource

@ExtendWith(SpringExtension::class)
@Import(JdbcCreateFantaTournamentRepositoryIT.TestConfig::class)
class JdbcCreateFantaTournamentRepositoryIT {

  @Autowired
  private lateinit var jdbcTemplate: JdbcTemplate

  private lateinit var repository: CreateFantaTournamentRepository

  @BeforeEach
  fun setUp() {

    repository = JdbcCreateFantaTournamentRepository(jdbcTemplate)
  }

  @Test
  fun `tournament successfully created`() {

    val dto = TournamentToCreateDto(startingTournamentId = 2,
                                    endingTournamentId = 10,
                                    tournamentYear = 2022)

    val expected = ValidFantaTournament(id = 1,
                                        startingTournamentId = 2,
                                        endingTournamentId = 10,
                                        tournamentYear = 2022)

    assertThat(repository.create(dto)).isEqualTo(expected)
  }

  companion object {

    private val mysqlContainer: MySQLContainer<*> = MySQLContainer("mysql:8.0")
        .apply {
          withDatabaseName("test_db")
          withUsername("test_user")
          withPassword("test_password")
          withCopyFileToContainer(
              MountableFile.forClasspathResource("test-containers/init-db.sql"),
              "/docker-entrypoint-initdb.d/init-db.sql"
          )
          start()
        }
  }

  @Configuration
  class TestConfig {

    @Bean
    fun dataSource(): DataSource =
        HikariConfig()
            .apply {
              jdbcUrl = mysqlContainer.jdbcUrl
              username = mysqlContainer.username
              password = mysqlContainer.password
              driverClassName = "com.mysql.cj.jdbc.Driver"
            }
            .let(::HikariDataSource)

    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate =
        JdbcTemplate(dataSource)
  }
}