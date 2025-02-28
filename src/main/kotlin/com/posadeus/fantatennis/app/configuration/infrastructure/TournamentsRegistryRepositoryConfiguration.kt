package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.domain.infrastructure.TournamentsRegistryRepository
import com.posadeus.fantatennis.infrastructure.client.tennistv.TennisTvClient
import com.posadeus.fantatennis.infrastructure.repository.tennistv.TennisTvTournamentsRegistryRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TournamentsRegistryRepositoryConfiguration {

  @Bean
  fun tennisTvTournamentsRegistryRepository(tennisTvGetClient: TennisTvClient): TournamentsRegistryRepository =
      TennisTvTournamentsRegistryRepository(tennisTvGetClient)
}