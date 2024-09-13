package com.posadeus.fantatennis.app.configuration.infrastructure.mysql

import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsTeamsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlFantaTournamentsTeamsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsTeamsDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MySqlTournamentsTeamsRepositoryConfiguration {

  @Bean
  open fun mySqlFantaTournamentsTeamsRepository(fantaTournamentsTeamsDao: FantaTournamentsTeamsDao): FantaTournamentsTeamsRepository =
      MySqlFantaTournamentsTeamsRepository(fantaTournamentsTeamsDao)
}