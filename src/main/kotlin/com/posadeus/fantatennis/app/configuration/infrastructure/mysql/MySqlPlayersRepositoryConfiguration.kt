package com.posadeus.fantatennis.app.configuration.infrastructure.mysql

import com.posadeus.fantatennis.domain.infrastructure.PlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlPlayersRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MySqlPlayersRepositoryConfiguration {

  @Bean
  fun mySqlPlayersRepository(playersDao: PlayersDao): PlayersRepository =
      MySqlPlayersRepository(playersDao)
}