package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
class JdbcConfig {

  @Bean
  fun jdbcTemplate(dataSource: DataSource): JdbcTemplate =
      JdbcTemplate(dataSource)

  @Bean
  fun namedParameterJdbcTemplate(dataSource: DataSource): NamedParameterJdbcTemplate =
      NamedParameterJdbcTemplate(dataSource)

  @Bean
  fun transactionManager(dataSource: DataSource): PlatformTransactionManager =
      DataSourceTransactionManager(dataSource)

}