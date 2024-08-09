package com.posadeus.fantatennis.app.configuration.infrastructure.mysql

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MySqlTeamRepositoryConfiguration {

  @Bean
  open fun mySqlTeamRepository(teamDao: MySqlTeamDao,
                               playerDao: MySqlPlayerDao): MySqlTeamRepository =
    MySqlTeamRepository(teamDao,
                        playerDao)
}