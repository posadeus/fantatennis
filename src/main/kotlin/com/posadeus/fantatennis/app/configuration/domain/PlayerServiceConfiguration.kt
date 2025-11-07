package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.domain.service.player.PersistPlayerService
import com.posadeus.fantatennis.domain.service.player.PlayerService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PlayerServiceConfiguration {

  @Bean
  fun playerService(jdbcRetrievePlayersRepository: RetrievePlayersRepository): PlayerService =
      PlayerService(jdbcRetrievePlayersRepository)

  @Bean
  fun persistPlayerService(jdbcPersistPlayersRepository: PersistPlayersRepository): PersistPlayerService =
      PersistPlayerService(jdbcPersistPlayersRepository)
}