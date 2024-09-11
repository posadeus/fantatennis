package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.infrastructure.client.tennistv.TennisTvClient
import com.posadeus.fantatennis.infrastructure.client.wimbledon.WimbledonClient
import com.posadeus.fantatennis.infrastructure.repository.tennistv.TennisTvTournamentInfoRepository
import com.posadeus.fantatennis.infrastructure.repository.wimbledon.WimbledonTournamentInfoRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TournamentRepositoryConfiguration {

  @Bean
  open fun tennisTvTournamentInfoRepository(tennisTvGetClient: TennisTvClient): TournamentInfoRepository =
      TennisTvTournamentInfoRepository(tennisTvGetClient)

  @Bean
  open fun wimbledonTournamentInfoRepository(wimbledonClient: WimbledonClient): TournamentInfoRepository =
      WimbledonTournamentInfoRepository(wimbledonClient)
}