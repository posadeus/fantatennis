package com.posadeus.fantatennis.app.configuration.infrastructure.mysql

import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlTeamRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.MySqlTeamDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MySqlTeamRepositoryConfiguration {

  @Bean
  open fun mySqlTeamRepository(teamDao: MySqlTeamDao,
                               playerDao: PlayersDao): MySqlTeamRepository =
    MySqlTeamRepository(teamDao,
                        playerDao)
}