package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.PersistPlayersRepository
import com.posadeus.fantatennis.domain.infrastructure.RetrievePlayersRepository
import com.posadeus.fantatennis.domain.service.player.PersistPlayerService
import com.posadeus.fantatennis.domain.service.player.RetrievePlayerService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PlayerServiceConfiguration {

  @Bean
  fun retrievePlayerService(jdbcRetrievePlayersRepository: RetrievePlayersRepository): RetrievePlayerService =
      RetrievePlayerService(jdbcRetrievePlayersRepository)

  @Bean
  fun persistPlayerService(jdbcPersistPlayersRepository: PersistPlayersRepository): PersistPlayerService =
      PersistPlayerService(jdbcPersistPlayersRepository)
}