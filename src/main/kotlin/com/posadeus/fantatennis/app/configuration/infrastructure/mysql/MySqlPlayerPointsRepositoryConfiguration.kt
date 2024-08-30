package com.posadeus.fantatennis.app.configuration.infrastructure.mysql

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlPlayerPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.PlayersPointsDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MySqlPlayerPointsRepositoryConfiguration {

  @Bean
  open fun mySqlPlayerPointsRepository(playersPointsDao: PlayersPointsDao): PlayerPointsRepository =
      MySqlPlayerPointsRepository(playersPointsDao)
}