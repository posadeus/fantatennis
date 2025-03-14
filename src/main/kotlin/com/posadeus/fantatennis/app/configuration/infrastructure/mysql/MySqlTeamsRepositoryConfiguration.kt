package com.posadeus.fantatennis.app.configuration.infrastructure.mysql

import com.posadeus.fantatennis.domain.infrastructure.TeamsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.MySqlTeamsRepository
import com.posadeus.fantatennis.infrastructure.repository.database.mysql.dao.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MySqlTeamsRepositoryConfiguration {

  @Bean
  fun mySqlTeamsRepository(fantaTeamsDao: FantaTeamsDao,
                           tournamentsDao: TournamentsDao,
                           playersDao: PlayersDao,
                           teamsDao: TeamsDao): TeamsRepository =
      MySqlTeamsRepository(fantaTeamsDao,
                           tournamentsDao,
                           playersDao,
                           teamsDao)
}