package com.posadeus.fantatennis.app.configuration.infrastructure.mysql

import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlFantaTeamsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTeamsDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MySqlFantaTeamsRepositoryConfiguration {

  @Bean
  open fun mySqlFantaTeamsRepository(fantaTeamsDao: FantaTeamsDao): FantaTeamsRepository =
      MySqlFantaTeamsRepository(fantaTeamsDao)
}