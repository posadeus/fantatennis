package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.domain.infrastructure.TournamentInfoRepository
import com.posadeus.fantatennis.infrastructure.client.ausopen.AusOpenClient
import com.posadeus.fantatennis.infrastructure.client.tennistv.TennisTvClient
import com.posadeus.fantatennis.infrastructure.client.usopen.UsOpenClient
import com.posadeus.fantatennis.infrastructure.client.wimbledon.WimbledonClient
import com.posadeus.fantatennis.infrastructure.repository.ausopen.AusOpenTournamentEventIdService
import com.posadeus.fantatennis.infrastructure.repository.ausopen.AusOpenTournamentInfoRepository
import com.posadeus.fantatennis.infrastructure.repository.tennistv.TennisTvTournamentInfoRepository
import com.posadeus.fantatennis.infrastructure.repository.usopen.UsOpenTournamentInfoRepository
import com.posadeus.fantatennis.infrastructure.repository.wimbledon.WimbledonTournamentInfoRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order

@Configuration
class TournamentInfoRepositoryConfiguration {

  @Bean
  @Order(99)
  fun tennisTvTournamentInfoRepository(tennisTvGetClient: TennisTvClient): TournamentInfoRepository =
      TennisTvTournamentInfoRepository(tennisTvGetClient)

  @Bean
  @Order(1)
  fun wimbledonTournamentInfoRepository(wimbledonClient: WimbledonClient): TournamentInfoRepository =
      WimbledonTournamentInfoRepository(wimbledonClient)

  @Bean
  @Order(2)
  fun usOpenTournamentInfoRepository(usOpenClient: UsOpenClient): TournamentInfoRepository =
      UsOpenTournamentInfoRepository(usOpenClient)

  @Bean
  @Order(3)
  fun ausOpenTournamentInfoRepository(ausOpenClient: AusOpenClient): TournamentInfoRepository =
      AusOpenTournamentInfoRepository(ausOpenClient, AusOpenTournamentEventIdService())
}