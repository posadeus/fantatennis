package com.posadeus.app.configuration.infrastructure

import com.posadeus.domain.infrastructure.AtpTourRepository
import com.posadeus.infrastructure.client.atptour.AtpTourClient
import com.posadeus.infrastructure.repository.atptour.AtpTourRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AtpTourRepositoryConfiguration {

  @Bean
  open fun atpTourRepository(atpTourClient: AtpTourClient): AtpTourRepository =
      AtpTourRepositoryImpl(atpTourClient)
}