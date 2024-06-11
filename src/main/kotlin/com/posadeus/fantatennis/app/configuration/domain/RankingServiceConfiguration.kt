package com.posadeus.fantatennis.app.configuration.domain

import com.posadeus.fantatennis.domain.infrastructure.AtpTourRepository
import com.posadeus.fantatennis.domain.service.ranking.RankingService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RankingServiceConfiguration {

  @Bean
  open fun rankingService(atpTourRepository: AtpTourRepository): RankingService =
      RankingService(atpTourRepository)
}