package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
class JdbcConfig {

  @Bean
  fun jdbcTemplate(dataSource: DataSource): JdbcTemplate =
      JdbcTemplate(dataSource)
}