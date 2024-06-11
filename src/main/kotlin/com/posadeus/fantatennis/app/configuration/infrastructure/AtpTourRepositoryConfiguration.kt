package com.posadeus.fantatennis.app.configuration.infrastructure

import com.posadeus.fantatennis.domain.infrastructure.AtpTourRepository
import com.posadeus.fantatennis.infrastructure.client.atptour.AtpTourClient
import com.posadeus.fantatennis.infrastructure.repository.atptour.AtpTourRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AtpTourRepositoryConfiguration {

  @Bean
  open fun atpTourRepository(atpTourClient: AtpTourClient): AtpTourRepository =
      AtpTourRepositoryImpl(atpTourClient)
}