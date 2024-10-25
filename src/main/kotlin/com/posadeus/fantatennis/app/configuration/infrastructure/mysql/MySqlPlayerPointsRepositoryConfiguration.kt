package com.posadeus.fantatennis.app.configuration.infrastructure.mysql

import com.posadeus.fantatennis.domain.infrastructure.PlayerPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlPlayerPointsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.PlayersPointsDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MySqlPlayerPointsRepositoryConfiguration {

  @Bean
  fun mySqlPlayerPointsRepository(playersPointsDao: PlayersPointsDao): PlayerPointsRepository =
      MySqlPlayerPointsRepository(playersPointsDao)
}