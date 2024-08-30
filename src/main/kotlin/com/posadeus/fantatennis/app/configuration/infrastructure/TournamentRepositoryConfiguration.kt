package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.domain.infrastructure.TournamentRepository
import com.posadeus.fantatennis.infrastructure.client.tennistv.TennisTvClient
import com.posadeus.fantatennis.infrastructure.repository.tennistv.TennisTvTournamentRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TournamentRepositoryConfiguration {

  @Bean
  open fun tennisTvTournamentRepository(tennisTvGetClient: TennisTvClient): TournamentRepository =
      TennisTvTournamentRepository(tennisTvGetClient)
}