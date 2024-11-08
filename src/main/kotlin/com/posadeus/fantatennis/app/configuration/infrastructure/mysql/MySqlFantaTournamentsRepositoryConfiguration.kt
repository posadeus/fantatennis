package com.posadeus.fantatennis.app.configuration.infrastructure.mysql

import com.posadeus.fantatennis.domain.infrastructure.FantaTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlFantaTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.FantaTournamentsDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MySqlFantaTournamentsRepositoryConfiguration {

  @Bean
  fun mySqlFantaTournamentsRepository(fantaTournamentsDao: FantaTournamentsDao): FantaTournamentsRepository =
      MySqlFantaTournamentsRepository(fantaTournamentsDao)
}