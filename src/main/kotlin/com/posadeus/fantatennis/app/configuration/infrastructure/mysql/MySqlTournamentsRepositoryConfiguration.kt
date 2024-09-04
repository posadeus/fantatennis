package com.posadeus.fantatennis.app.configuration.infrastructure.mysql

import com.posadeus.fantatennis.domain.infrastructure.TournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlTournamentsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.TournamentsDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MySqlTournamentsRepositoryConfiguration {

  @Bean
  open fun mySqlTournamentsRepository(tournamentsDao: TournamentsDao): TournamentsRepository =
      MySqlTournamentsRepository(tournamentsDao)
}