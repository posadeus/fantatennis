package com.posadeus.fantatennis.app.configuration.infrastructure.mysql

import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlFantaTeamsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTeamsDao
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsTeamsDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MySqlFantaTeamsRepositoryConfiguration {

  @Bean
  fun mySqlFantaTeamsRepository(fantaTeamsDao: FantaTeamsDao,
                                fantaTournamentsTeamsDao: FantaTournamentsTeamsDao): FantaTeamsRepository =
      MySqlFantaTeamsRepository(fantaTeamsDao,
                                fantaTournamentsTeamsDao)
}