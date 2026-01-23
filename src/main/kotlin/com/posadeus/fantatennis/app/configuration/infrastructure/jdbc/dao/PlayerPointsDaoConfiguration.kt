package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc.dao

import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.PlayerPointsDao
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dao.playerpoints.JdbcPlayerPointsDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class PlayerPointsDaoConfiguration {

  @Bean
  fun jdbcPlayerPointsDao(namedParameterJdbcTemplate: NamedParameterJdbcTemplate): PlayerPointsDao =
      JdbcPlayerPointsDao(namedParameterJdbcTemplate)
}