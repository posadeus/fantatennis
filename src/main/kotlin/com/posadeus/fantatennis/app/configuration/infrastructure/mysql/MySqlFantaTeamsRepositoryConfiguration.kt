package com.posadeus.fantatennis.app.configuration.infrastructure.mysql

import com.posadeus.fantatennis.domain.infrastructure.FantaTeamsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlFantaTeamsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MySqlFantaTeamsRepositoryConfiguration {

  @Bean
  fun mySqlFantaTeamsRepository(fantaTeamsDao: FantaTeamsDao,
                                fantaTournamentsTeamsDao: FantaTournamentsTeamsDao,
                                fantaTournamentsDao: FantaTournamentsDao): FantaTeamsRepository =
      MySqlFantaTeamsRepository(fantaTeamsDao,
                                fantaTournamentsTeamsDao,
                                fantaTournamentsDao)
}