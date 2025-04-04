package com.posadeus.fantatennis.infrastructure.repository.database.jdbc.it

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.MountableFile
import javax.sql.DataSource

@Configuration
class IntegrationTestConfiguration {

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
}