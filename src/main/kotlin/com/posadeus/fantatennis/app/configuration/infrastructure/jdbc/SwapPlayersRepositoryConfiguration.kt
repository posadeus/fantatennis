package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.github.benmanes.caffeine.cache.Cache
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.domain.model.TeamId
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcSwapPlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.dto.JdbcTeamDto
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class SwapPlayersRepositoryConfiguration {

  @Bean
  fun jdbcSwapPlayersRepository(jdbcTemplate: JdbcTemplate,
                                namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
                                teamCache: Cache<Set<TeamId>, List<JdbcTeamDto>>): SwapPlayersRepository =
      JdbcSwapPlayersRepository(jdbcTemplate,
                                namedParameterJdbcTemplate,
                                teamCache)
}
