package com.posadeus.fantatennis.app.configuration.infrastructure.jdbc

import com.posadeus.fantatennis.domain.infrastructure.RetrieveFantaTeamRepository
import com.posadeus.fantatennis.domain.infrastructure.SwapPlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.jdbc.JdbcSwapPlayersRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class SwapPlayersRepositoryConfiguration {

  @Bean
  fun jdbcSwapPlayersRepository(jdbcRetrieveFantaTeamRepository: RetrieveFantaTeamRepository,
                                jdbcTemplate: JdbcTemplate): SwapPlayersRepository =
      JdbcSwapPlayersRepository(jdbcRetrieveFantaTeamRepository,
                                jdbcTemplate)
}